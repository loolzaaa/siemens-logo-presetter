package ru.loolzaaa.siemens.logopresetter.scan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DeviceScannerManager {

    private static DeviceScannerManager deviceScannerManager;

    private DeviceScannerManager() {}

    public static DeviceScannerManager getInstance() {
        if (deviceScannerManager == null) {
            deviceScannerManager = new DeviceScannerManager();
        }
        return deviceScannerManager;
    }

    public List<DeviceInfo> scan(String customHost) {
        final String hostname = customHost != null ? customHost : getBindHostname();
        System.out.println("Current hostname: " + hostname);
        DeviceScanner ds = new DeviceScanner(hostname);
        ds.sendDatagram();
        return ds.receiveDatagram();
    }

    private String getBindHostname() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
