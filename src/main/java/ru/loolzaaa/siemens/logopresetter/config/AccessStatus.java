package ru.loolzaaa.siemens.logopresetter.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessStatus {

    private static final int S7_ENABLE_POS = 0;
    private static final int S7_SECURED_POS = 1;
    private static final int MODBUS_ENABLE_POS = 2;
    private static final int MODBUS_SECURED_POS = 3;

    private ChannelSetting s7Access;
    private ChannelSetting modbusAccess;

    public static AccessStatus load(byte[] bytes) {
        AccessStatus result = new AccessStatus();
        int bit0 = bytes[0] >> S7_ENABLE_POS & 1;
        int bit1 = bytes[0] >> S7_SECURED_POS & 1;
        int bit2 = bytes[0] >> MODBUS_ENABLE_POS & 1;
        int bit3 = bytes[0] >> MODBUS_SECURED_POS & 1;
        result.setS7Access(new ChannelSetting(bit0 == 1, bit1 == 1));
        result.setModbusAccess(new ChannelSetting(bit2 == 1, bit3 == 1));
        return result;
    }
}
