package ru.loolzaaa.siemens.logopresetter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import ru.loolzaaa.siemens.logopresetter.config.AccessControlSettings;
import ru.loolzaaa.siemens.logopresetter.config.LogoDataTransferWrapper;
import ru.loolzaaa.siemens.logopresetter.hardware.BaseHardware;
import ru.loolzaaa.siemens.logopresetter.hardware.Hardware;
import ru.loolzaaa.siemens.logopresetter.net.DataTransfer;
import ru.loolzaaa.siemens.logopresetter.net.HttpDataTransfer;
import ru.loolzaaa.siemens.logopresetter.scan.DeviceInfo;
import ru.loolzaaa.siemens.logopresetter.scan.DeviceScannerManager;
import ru.loolzaaa.siemens.logopresetter.server.PresetterHttpServer;
import ru.loolzaaa.siemens.logopresetter.util.IPV4Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LogoPresetter {
    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("siemens-logo-presetter")
                .locale(Locale.US)
                .build()
                .description("Settings base properties for Siemens Logo 8 and newer.")
                .defaultHelp(true);
        parser.addArgument("mode")
                .choices("scan", "show", "set", "server")
                .required(true)
                .help("Application operation mode.\n" +
                        "Scan - start scan process.\n" +
                        "Show - receive details about device.\n" +
                        "Set - apply new settings to device.\n" +
                        "Server - start presetter int server mode on 12000 port");

        parser.addArgument("--ip")
                .help("current ip address for device");
        parser.addArgument("--set-ip")
                .metavar("IP")
                .help("new ip address for device");
        parser.addArgument("--set-mask")
                .metavar("MASK")
                .help("new subnet mask for device");
        parser.addArgument("--set-gateway")
                .metavar("GATEWAY")
                .help("new gateway address for device");

        parser.addArgument("--host")
                .metavar("HOST")
                .help("custom host address for scan purposes");

        Namespace n = null;
        try {
            n = parser.parseArgs(args);
            System.out.println("Current mode: " + n.getString("mode"));

            boolean unknownIpAddress = n.get("ip") == null;
            if ("show".equals(n.get("mode")) && unknownIpAddress) {
                throw new ArgumentParserException("You must specify --ip argument for 'show' mode", parser);
            }

            boolean incorrectSetParams = n.get("set_ip") == null || n.get("set_mask") == null || n.get("set_gateway") == null;
            if ("set".equals(n.get("mode")) && (unknownIpAddress || incorrectSetParams)) {
                throw new ArgumentParserException("You must specify --ip and all --set-XXX arguments for 'set' mode", parser);
            }
            if ("set".equals(n.get("mode")) && incorrectSetParams) {
                try {
                    if (IPV4Utils.getInstance().formatToInt(n.getString("set_ip")) == 0) {
                        throw new ArgumentParserException("New IP Address format error", parser);
                    }
                    if (IPV4Utils.getInstance().formatToInt(n.getString("set_mask")) == 0) {
                        throw new ArgumentParserException("New Mask Address format error", parser);
                    }
                    if (IPV4Utils.getInstance().formatToInt(n.getString("set_gateway")) == 0) {
                        throw new ArgumentParserException("New Gateway Address format error", parser);
                    }
                } catch (IllegalArgumentException e) {
                    throw new ArgumentParserException(e.getMessage(), parser);
                }
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final String mode = n.getString("mode");
        LogoPresetter logoPresetter = new LogoPresetter();
        if ("show".equals(mode)) {
            logoPresetter.showDeviceDetails(n.getString("ip"));
        } else if ("set".equals(mode)) {
            BaseHardware.IPConfig ipConfig = new BaseHardware.IPConfig();
            ipConfig.ip = n.getString("set_ip");
            ipConfig.mask = n.getString("set_mask");
            ipConfig.gateway = n.getString("set_gateway");
            logoPresetter.setDeviceProperties(n.getString("ip"), ipConfig);
        } else if ("scan".equals(mode)) {
            logoPresetter.scanAllDevices(n.getString("host"));
        } else if ("server".equals(mode)) {
            logoPresetter.startServer(logoPresetter);
        } else {
            throw new IllegalArgumentException("Incorrect mode: " + mode);
        }
    }

    public List<Object> showDeviceDetails(String ipAddress) {
        List<Object> deviceDetails = new ArrayList<>(3);
        DataTransfer dataTransfer = null;
        try {
            dataTransfer = HttpDataTransfer.openConnection(ipAddress, -1);
            String fwVersion = dataTransfer.getFWVersion();
            String[] strs = fwVersion.split("\\.");
            fwVersion = strs[0] + "." + strs[1] + "." + strs[2];
            System.out.println(fwVersion);
            deviceDetails.add(fwVersion);

            BaseHardware.IPConfig ipConfig = ((BaseHardware) dataTransfer.getHardware()).getIPConfig(dataTransfer);
            System.out.println("Ip address: " + ipConfig.ip);
            System.out.println("Mask: " + ipConfig.mask);
            System.out.println("Gateway: " + ipConfig.gateway);
            deviceDetails.add(ipConfig);

            if (!dataTransfer.isTransmissionPossible()) {
                throw new IllegalStateException("Transmission not possible");
            }
            LogoDataTransferWrapper logoDataTransferWrapper = new LogoDataTransferWrapper(dataTransfer);
            AccessControlSettings accessControlSettings = logoDataTransferWrapper.getAccessControlSettings();
            dataTransfer.startLogo();

            System.out.println("S7 access: " + (accessControlSettings.getS7Access().isEnabled() ? "enabled" : "disabled"));
            System.out.println("Modbus access: " + (accessControlSettings.getModbusAccess().isEnabled() ? "enabled" : "disabled"));
            deviceDetails.add(accessControlSettings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (dataTransfer != null) {
                dataTransfer.closePort();
            }
        }
        return deviceDetails;
    }

    public void setDeviceProperties(String ipAddress, BaseHardware.IPConfig ipConfig) {
        DataTransfer dataTransfer = null;
        try {
            dataTransfer = HttpDataTransfer.openConnection(ipAddress, -1);
            if (dataTransfer instanceof HttpDataTransfer) {
                if (!((HttpDataTransfer) dataTransfer).prepareDt()) {
                    throw new RuntimeException("Can't refresh data transfer!");
                }
            }

            Hardware hw = dataTransfer.getHardware();
            if (!dataTransfer.isTransmissionPossible()) {
                throw new IllegalStateException("Transmission not possible");
            }
            ((BaseHardware) hw).setIPConfig(dataTransfer, ipConfig, true);
            System.out.println("IP configured successfully.");

            if (!dataTransfer.isTransmissionPossible()) {
                throw new IllegalStateException("Transmission not possible");
            }
            LogoDataTransferWrapper logoDataTransferWrapper = new LogoDataTransferWrapper(dataTransfer);
            AccessControlSettings accessControlSettings = logoDataTransferWrapper.getAccessControlSettings();
            accessControlSettings.getS7Access().setEnabled(true);
            logoDataTransferWrapper.setAccessControlSettings(accessControlSettings);
            System.out.println("Access settings configured successfully.");

            dataTransfer.startLogo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (dataTransfer != null) {
                dataTransfer.closePort();
            }
        }
    }

    public List<DeviceInfo> scanAllDevices(String customHost) {
        List<DeviceInfo> scan = DeviceScannerManager.getInstance().scan(customHost);
        System.out.println(scan);
        return scan;
    }

    public void startServer(LogoPresetter logoPresetter) {
        PresetterHttpServer presetterHttpServer = new PresetterHttpServer(logoPresetter);
        try {
            presetterHttpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
