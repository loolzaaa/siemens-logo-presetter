package ru.loolzaaa.siemens.logopresetter.hardware;

import java.util.List;

public class LogoHardwareFactory {

    private static final List<Integer> supportedHardwareIds = List.of(
            96,         // LOGO8
            97,         // LOGO8UDF
            129,        // LOGO8Update1
            -16777087,  // LOGO8Update1UDF
            131,        // LOGO8Update2
            -16777085   // LOGO8Update2UDF
    );

    private static LogoHardwareFactory instance;

    private LogoHardwareFactory() {}

    public static LogoHardwareFactory getInstance() throws IllegalArgumentException {
        if (instance == null) {
            instance = new LogoHardwareFactory();
        }
        return instance;
    }

    public Hardware getHardware(int hardwareID) {
        if (supportedHardwareIds.contains(hardwareID)) {
            return new BaseHardware(hardwareID);
        } else {
            return new Unknown(hardwareID);
        }
    }
}
