package ru.loolzaaa.siemens.logopresetter.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class AccessControlSettings {

    private static final int CHANNEL_LENGTH = 4;
    private static final int USER_LENGTH = 52;
    private static final int WEBSERVER_ENABLE_POS = 0;
    private static final int WEBSERVER_SECURED_POS = 1;
    private static final int TDE_ENABLE_POS = 2;
    private static final int TDE_SECURED_POS = 3;
    private static final int CLOUD_ENABLE_POS = 4;
    private static final int S7_ENABLE_POS = 5;
    private static final int MODBUS_ENABLE_POS = 6;

    private ChannelSetting webAccess;
    private ChannelSetting tdeAccess;
    private ChannelSetting cloudAccess;
    private ChannelSetting s7Access;
    private ChannelSetting modbusAccess;

    private UserProfile lscUser;
    private UserProfile webUser;
    private UserProfile webGuestUser;
    private UserProfile appUser;
    private UserProfile tdeOnBoardUser;

    public static AccessControlSettings load(byte[] bytes) {
        AccessControlSettings result = new AccessControlSettings();
        int bit0 = bytes[0] >> WEBSERVER_ENABLE_POS & 1;
        int bit1 = bytes[0] >> WEBSERVER_SECURED_POS & 1;
        int bit2 = bytes[0] >> TDE_ENABLE_POS & 1;
        int bit3 = bytes[0] >> TDE_SECURED_POS & 1;
        int bit4 = bytes[0] >> CLOUD_ENABLE_POS & 1;
        int bit5 = bytes[0] >> S7_ENABLE_POS & 1;
        int bit6 = bytes[0] >> MODBUS_ENABLE_POS & 1;
        result.setWebAccess(new ChannelSetting(bit0 == 1, bit1 == 1));
        result.setTdeAccess(new ChannelSetting(bit2 == 1, bit3 == 1));
        result.setCloudAccess(new ChannelSetting(bit4 == 1, true));
        result.setS7Access(new ChannelSetting(bit5 == 1, false));
        result.setModbusAccess(new ChannelSetting(bit6 == 1, false));
        int index = 0;
        result.setLscUser(UserProfile.load(Arrays.copyOfRange(bytes, CHANNEL_LENGTH + USER_LENGTH * index, CHANNEL_LENGTH + USER_LENGTH * (1 + index++))));
        result.setAppUser(UserProfile.load(Arrays.copyOfRange(bytes, CHANNEL_LENGTH + USER_LENGTH * index, CHANNEL_LENGTH + USER_LENGTH * (1 + index++))));
        result.setWebUser(UserProfile.load(Arrays.copyOfRange(bytes, CHANNEL_LENGTH + USER_LENGTH * index, CHANNEL_LENGTH + USER_LENGTH * (1 + index++))));
        result.setWebGuestUser(UserProfile.load(Arrays.copyOfRange(bytes, CHANNEL_LENGTH + USER_LENGTH * index, CHANNEL_LENGTH + USER_LENGTH * (1 + index++))));
        result.setTdeOnBoardUser(UserProfile.load(Arrays.copyOfRange(bytes, CHANNEL_LENGTH + USER_LENGTH * index, CHANNEL_LENGTH + USER_LENGTH * (1 + index++))));
        return result;
    }

    public byte[] getBytes() {
        byte[] result = new byte[316];
        if (webAccess.isEnabled()) {
            result[0] = (byte)(result[0] | 1);
        } else {
            result[0] &= -2;
        }

        if (webAccess.isSecured()) {
            result[0] = (byte)(result[0] | 2);
        } else {
            result[0] &= -3;
        }

        if (tdeAccess.isEnabled()) {
            result[0] = (byte)(result[0] | 4);
        } else {
            result[0] &= -5;
        }

        if (tdeAccess.isSecured()) {
            result[0] = (byte)(result[0] | 8);
        } else {
            result[0] &= -9;
        }

        if (cloudAccess.isEnabled()) {
            result[0] = (byte)(result[0] | 16);
        } else {
            result[0] &= -17;
        }

        if (s7Access.isEnabled()) {
            result[0] = (byte)(result[0] | 32);
        } else {
            result[0] &= -33;
        }

        if (modbusAccess.isEnabled()) {
            result[0] = (byte)(result[0] | 64);
        } else {
            result[0] &= -65;
        }

        byte[] bLSCUser = lscUser.getBytes();
        System.arraycopy(bLSCUser, 0, result, 4, bLSCUser.length);

        byte[] bAPPUser = appUser.getBytes();
        System.arraycopy(bAPPUser, 0, result, 56, bAPPUser.length);

        byte[] bWebUser = webUser.getBytes();
        System.arraycopy(bWebUser, 0, result, 108, bWebUser.length);

        byte[] bWebGuestUser = webGuestUser.getBytes();
        System.arraycopy(bWebGuestUser, 0, result, 160, bWebGuestUser.length);

        byte[] bTDEOnBoardUser = tdeOnBoardUser.getBytes();
        System.arraycopy(bTDEOnBoardUser, 0, result, 212, bTDEOnBoardUser.length);

        return result;
    }
}
