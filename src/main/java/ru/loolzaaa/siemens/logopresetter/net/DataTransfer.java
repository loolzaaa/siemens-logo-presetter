package ru.loolzaaa.siemens.logopresetter.net;

import ru.loolzaaa.siemens.logopresetter.config.DeviceType;
import ru.loolzaaa.siemens.logopresetter.hardware.Hardware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class DataTransfer {

    public static final int TS_RUN = 1;
    public static final int TS_STOP = 2;
    public static final int TS_EDITING = 3;
    public static final int TS_PARAM = 32;

    protected InputStream fInputStream = null;
    protected OutputStream fOutputStream = null;

    protected Hardware fRemoteHardware = null;

    public abstract void closePort();

    public abstract Hardware getHardware();

    public abstract String getFWVersion();

    public abstract boolean isTransmissionPossible();

    public abstract void startLogo() throws IOException;

    public abstract void stopLogo() throws IOException;

    public abstract DeviceType getDeviceType();

    public abstract void rebuildConnection() throws IOException;

    public abstract byte[] readBytes() throws IOException;

    public abstract void writeBytes(byte[] bytes) throws IOException;
}
