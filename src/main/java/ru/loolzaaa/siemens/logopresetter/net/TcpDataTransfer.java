package ru.loolzaaa.siemens.logopresetter.net;

import ru.loolzaaa.siemens.logopresetter.config.DeviceType;
import ru.loolzaaa.siemens.logopresetter.hardware.Hardware;
import ru.loolzaaa.siemens.logopresetter.hardware.LogoHardwareFactory;
import ru.loolzaaa.siemens.logopresetter.hardware.Unknown;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpDataTransfer extends DataTransfer {

    Socket fSocket = null;
    public static final int RESULT_NO_SERVICE_0BA8 = -101;
    public static final int RESULT_NO_SERVICE_0BA81_LATER = -1151;
    public static final int TCP_SOTIMEOUT = 15000;
    public static final int DEFAULT_PORT = 10005;
    private InetAddress fCurrentAddress;

    public static DataTransfer openConnection(String ipAddress, int portNr) throws IOException {
        int dstPort = portNr <= 0 ? DEFAULT_PORT : portNr;
        InetAddress destinationIP = InetAddress.getByName(ipAddress);
        return new TcpDataTransfer(destinationIP, dstPort);
    }

    protected TcpDataTransfer() {
    }

    protected TcpDataTransfer(InetAddress destinationIP, int socketNr) throws IOException {
        this.fCurrentAddress = destinationIP;
        this.fSocket = new Socket();
        this.fSocket.connect(new InetSocketAddress(destinationIP, socketNr), 6000);
        this.fSocket.setTcpNoDelay(false);
        this.fSocket.setSoTimeout(TCP_SOTIMEOUT);
        this.fInputStream = this.fSocket.getInputStream();
        this.fOutputStream = this.fSocket.getOutputStream();
    }

    public void closePort() {
        if (fSocket != null) {
            try {
                fInputStream.close();
                fOutputStream.close();
                fSocket.close();
            } catch (Exception ignored) {
            } finally {
                fSocket = null;
            }
        }

    }

    public void reConnect() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
        fRemoteHardware = getRemoteHardware();
    }

    public void rebuildConnection() throws IOException {
        closePort();
        fSocket = new Socket();
        fSocket.connect(new InetSocketAddress(fCurrentAddress, DEFAULT_PORT), 6000);
        fSocket.setTcpNoDelay(false);
        fSocket.setSoTimeout(TCP_SOTIMEOUT);
        fInputStream = fSocket.getInputStream();
        fOutputStream = fSocket.getOutputStream();
        reConnect();
    }

    public Hardware getHardware() {
        if (fRemoteHardware == null || fRemoteHardware instanceof Unknown) {
            fRemoteHardware = getRemoteHardware();
        }
        return fRemoteHardware;
    }

    protected Hardware getRemoteHardware() {
        int remoteHardwareID = uploadHardwareID();
        if (remoteHardwareID < 0) {
            remoteHardwareID += 256;
        }
        return LogoHardwareFactory.getInstance().getHardware(remoteHardwareID);
    }

    public DeviceType getDeviceType() {
        LogoRequest req = new LogoRequest.Builder("GetHWType").build();
        try {
            writeBytes(req.getRequestByte());
            LogoResponse resp = new LogoResponse(readBytes(), true);
            if (resp.getResult() == 0) {
                byte type = resp.getByteFromParameter(0);
                return DeviceType.valueOf(type);
            } else if (RESULT_NO_SERVICE_0BA81_LATER != resp.getResult() && RESULT_NO_SERVICE_0BA8 != resp.getResult()) {
                throw new IllegalArgumentException("Incorrect result length");
            } else {
                return DeviceType.BM;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTransmissionPossible() {
        try {
            Hardware hw = getHardware();
            if (hw instanceof Unknown) {
                System.out.println("Unknown hardware id: " + hw.getID());
                return false;
            } else {
                int deviceState = getDeviceState();
                if (deviceState != TS_RUN && deviceState != TS_PARAM) {
                    return true;
                } else {
                    //TODO:
                    int rv = 0; //JOptionPane.showConfirmDialog(component, Language.getString("error.com.deviceInRun", "Change to STOP?"), Language.getString("hardware.family.name", "Device"), 0);
                    if (rv == 0) {
                        rebuildConnection();
                        stopLogo();
                        Thread.sleep(500L);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    public int getDeviceState() throws IOException {
        LogoRequest req = new LogoRequest.Builder("CheckState").syncCallFlg(1).needReturnFlg(1).requestFlg(1).build();

        byte[] responseBytes;
        writeBytes(req.getRequestByte());
        responseBytes = readBytes();

        return responseBytes[16] & 255;
    }

    private void switchLogoMode() throws IOException {
        LogoRequest req = new LogoRequest.Builder("SwitchMode").syncCallFlg(1).needReturnFlg(1).requestFlg(1).build();
        writeBytes(req.getRequestByte());
        LogoResponse resp = new LogoResponse(readBytes(), true);
        if (resp.getLength() == 16) {
            System.out.println(resp.getResult());
        }
    }

    public void startLogo() throws IOException {
        int state = getDeviceState();
        if (state == TS_EDITING) {
            throw new IllegalStateException("in editing state");
        } else if (state != TS_RUN) {
            switchLogoMode();
        }
    }

    public void stopLogo() throws IOException {
        switchLogoMode();
    }

    protected int uploadHardwareID() {
        LogoRequest req = new LogoRequest.Builder("GetHWId").syncCallFlg(1).build();
        try {
            if (fInputStream != null) {
                fInputStream.skip(fInputStream.available());
            }
            writeBytes(req.getRequestByte());
            byte[] responseBytes = readBytes();
            return responseBytes[16];
        } catch (IOException e) {
            return -1;
        }
    }

    public byte[] readBytes() throws IOException {
        byte[] context = new byte[16];
        fInputStream.read(context);
        LogoResponse logoResponse = new LogoResponse(context, false);
        int length = logoResponse.getLength();
        if (length < 16) {
            throw new IOException("BM Response data is not correct");
        } else if (length == 16) {
            return context;
        } else {
            boolean isLengthMatch = false;
            long maxTimeMillis = System.currentTimeMillis() + 3000L;

            while (System.currentTimeMillis() < maxTimeMillis) {
                if (fInputStream.available() >= length - 16) {
                    isLengthMatch = true;
                    break;
                }
            }

            if (!isLengthMatch) {
                throw new IOException("BM Response data is not correct");
            } else {
                byte[] parameterBytes = new byte[length - 16];
                fInputStream.read(parameterBytes);
                byte[] wholeBytes = new byte[length];

                int i;
                for (i = 0; i < 16; ++i) {
                    wholeBytes[i] = context[i];
                }

                for (i = 16; i < length; ++i) {
                    wholeBytes[i] = parameterBytes[i - 16];
                }

                return wholeBytes;
            }
        }
    }

    public void writeBytes(byte[] bytes) throws IOException {
        fOutputStream.write(bytes);
    }

    public String getFWVersion() {
        String version = "";
        LogoRequest req = new LogoRequest.Builder("GetFW").build();
        try {
            writeBytes(req.getRequestByte());
            LogoResponse resp = new LogoResponse(readBytes(), true);
            if ((resp.getLength() == 32 || resp.getLength() == 36) && resp.getResult() == 0) {
                byte[] verBytes = resp.getParameter();
                version = (new String(verBytes, 0, verBytes.length - 3)).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
}
