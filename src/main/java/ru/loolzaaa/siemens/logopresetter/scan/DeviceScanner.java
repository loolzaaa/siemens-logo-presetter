package ru.loolzaaa.siemens.logopresetter.scan;

import ru.loolzaaa.siemens.logopresetter.config.DeviceType;
import ru.loolzaaa.siemens.logopresetter.util.IPV4Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class DeviceScanner {

    private DatagramSocket ds;
    private DatagramSocket receiveDs;

    private InetAddress broadcastAddress;

    private final byte[] buffer = new byte[30];

    private static final int PLATFORM = 2; //Windows, Android = 8, Linux = 1 TODO

    public DeviceScanner(String hostname) {
        try {
            this.ds = new DatagramSocket(new InetSocketAddress(hostname, 0));
            this.ds.setBroadcast(true);
            this.broadcastAddress = InetAddress.getByName("255.255.255.255");
            if (PLATFORM == 2) {
                this.receiveDs = new DatagramSocket(new InetSocketAddress(hostname, this.ds.getLocalPort() + 1));
            } else {
                this.receiveDs = new DatagramSocket(this.ds.getLocalPort() + 1);
            }
            this.receiveDs.setBroadcast(true);
            this.receiveDs.setSoTimeout(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDatagramSocket() {
        if (ds != null) {
            ds.close();
            ds = null;
        }
        if (receiveDs != null) {
            receiveDs.close();
            receiveDs = null;
        }
    }

    public void sendDatagram() {
        final int REMOTE_SERVER_PORT = 10006;
        final byte[] DATA = new byte[]{1, 0, 0, 0};
        DatagramPacket sendDatagramPacket = new DatagramPacket(DATA, DATA.length, broadcastAddress, REMOTE_SERVER_PORT);

        try {
            if (ds != null) {
                ds.send(sendDatagramPacket);
                if (PLATFORM == 2 && receiveDs != null) {
                    receiveDs.send(sendDatagramPacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<DeviceInfo> receiveDatagram() {
        configRPFilter4LinuxAndMac();
        List<DeviceInfo> deviceInfoList = new ArrayList<>(10);
        DatagramPacket receiveDatagramPacket = new DatagramPacket(buffer, buffer.length);

        while(true) {
            try {
                receiveDs.receive(receiveDatagramPacket);
                DeviceInfo deviceInfo = analysisDatagram();
                if (deviceInfo != null && !deviceInfoList.contains(deviceInfo)) {
                    deviceInfoList.add(deviceInfo);
                }
            } catch (Exception e) {
                closeDatagramSocket();
                return deviceInfoList;
            }
        }
    }

    private DeviceInfo analysisDatagram() {
        int index = 0;
        int cmdID = buffer[index++];
        int srcID = buffer[index++];
        int sessionID = buffer[index++] & 255 << 8 + buffer[index++];
        StringBuilder fromAddress = new StringBuilder();
        DeviceType deviceType = DeviceType.BM;
        int fwSeries;
        int hwId;
        if (cmdID == 7) { //TDE_CMDID
            deviceType = DeviceType.valueOf(buffer[index++] & 255);
            hwId = buffer[index++] & 255;

            for(fwSeries = 0; fwSeries < 4; ++fwSeries) {
                fromAddress.append(buffer[index++] & 255);
                if (fwSeries < 3) {
                    fromAddress.append(".");
                }
            }
        } else {
            for(fwSeries = 0; fwSeries < 4; ++fwSeries) {
                fromAddress.append(buffer[index++] & 255);
                if (fwSeries < 3) {
                    fromAddress.append(".");
                }
            }

            hwId = buffer[index++] & 255;
        }

        fwSeries = buffer[index++] & 255;
        int msStatus = buffer[index++] & 255;
        StringBuilder logoMacAddress = new StringBuilder();

        int ip;
        String logoIpAddress;
        for(ip = 0; ip < 6; ++ip) {
            logoIpAddress = String.format("%02X", buffer[index++]);
            logoMacAddress.append(logoIpAddress);
            if (ip < 5) {
                logoMacAddress.append("-");
            }
        }

        ip = 0;

        for(int i = 0; i < 4; ++i) {
            ip = ip << 8 | buffer[index++] & 255;
        }

        logoIpAddress = IPV4Utils.getInstance().formatToString(ip);
        int mask = 0;

        for(int i = 0; i < 4; ++i) {
            mask = mask << 8 | buffer[index++] & 255;
        }

        String logoMask = IPV4Utils.getInstance().formatToString(mask);
        int gate = 0;

        for(int i = 0; i < 4; ++i) {
            gate = gate << 8 | buffer[index++] & 255;
        }

        String logoGateway = IPV4Utils.getInstance().formatToString(gate);
        if (fromAddress.toString().equals(ds.getLocalAddress().getHostAddress())) {
            DeviceInfo device = new DeviceInfo();
            device.setHwId(hwId);
            device.setCmdId(cmdID);
            device.setSrcId(srcID);
            device.setSessionId(sessionID);
            device.setIp(logoIpAddress);
            device.setMac(logoMacAddress.toString());
            device.setMask(logoMask);
            device.setGateway(logoGateway);
            device.setFwSeries(fwSeries);
            device.setDeviceType(deviceType);
            device.setDeviceState(msStatus);
            return device;
        } else {
            return null;
        }
    }

    private static void configRPFilter4LinuxAndMac() {
        if (PLATFORM == 1) {
            try {
                Runtime.getRuntime().exec("sudo sysctl -w net.ipv4.conf.all.rp_filter=0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
