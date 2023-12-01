package ru.loolzaaa.siemens.logopresetter.config;

public enum DeviceType {
    BM,
    TDE,
    OTHER;

    public static DeviceType valueOf(int value) {
        switch (value) {
            case 0:
                return BM;
            case 1:
                return TDE;
            default:
                return OTHER;
        }
    }
}
