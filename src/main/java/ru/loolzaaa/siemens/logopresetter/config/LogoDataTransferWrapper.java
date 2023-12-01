package ru.loolzaaa.siemens.logopresetter.config;

import ru.loolzaaa.siemens.logopresetter.net.DataTransfer;
import ru.loolzaaa.siemens.logopresetter.net.LogoRequest;
import ru.loolzaaa.siemens.logopresetter.net.LogoResponse;

import java.io.IOException;

public class LogoDataTransferWrapper {

    private final DataTransfer dataTransfer;

    public LogoDataTransferWrapper(DataTransfer dataTransfer) {
        if (dataTransfer.getDeviceType() == DeviceType.BM) {
            this.dataTransfer = dataTransfer;
        } else {
            throw new IllegalArgumentException("The target device does not match!");
        }
    }

    public AccessControlSettings getAccessControlSettings() throws IOException {
        LogoRequest req = (new LogoRequest.Builder("GetPrflV2")).build();
        dataTransfer.writeBytes(req.getRequestByte());
        LogoResponse resp = new LogoResponse(dataTransfer.readBytes(), true);
        return resp.getResult() == 0 ? AccessControlSettings.load(resp.getParameter()) : null;
    }

    public void setAccessControlSettings(AccessControlSettings accessControlSettings) throws IOException {
        LogoRequest req = (new LogoRequest.Builder("SetPrflV2")).build();
        req.setByteArrayParameter(accessControlSettings.getBytes());
        dataTransfer.writeBytes(req.getRequestByte());
        LogoResponse resp = new LogoResponse(dataTransfer.readBytes(), true);
        if (resp.getResult() != 0) {
            System.out.print("Operation is failed! The error code is" + resp.getResult());
        }
    }

    public AccessStatus getAccessStatus() throws IOException {
        LogoRequest req = (new LogoRequest.Builder("GetAcsState")).build();
        dataTransfer.writeBytes(req.getRequestByte());
        LogoResponse resp = new LogoResponse(dataTransfer.readBytes(), true);
        return resp.getResult() == 0 ? AccessStatus.load(resp.getParameter()) : null;
    }
}
