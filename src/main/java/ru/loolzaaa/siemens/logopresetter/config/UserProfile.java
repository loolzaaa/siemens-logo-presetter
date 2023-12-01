package ru.loolzaaa.siemens.logopresetter.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class UserProfile {

    private static final int FLAG_LENGTH = 2;
    private static final int RESERVED_LEGNTH = 2;
    private static final int USERNAME_LENGTH = 16;
    private static final int PASSWROD_LENGTH = 32;
    private static final int USER_ENABLE_POS = 0;
    private static final int USER_PASSWORD_ENABLE_POS = 1;
    private static final int USER_MULTI_LOGIN_ENABLE_POS = 2;

    private boolean enabled = false;
    private boolean passwordEnabled = false;
    private boolean multiLogInEnabled = false;
    private byte acl;

    private String password = "";
    private String userName = "LSC User";

    public static UserProfile load(byte[] bytes) {
        String user = (new String(Arrays.copyOfRange(bytes, 4, 20))).trim();
        String pwd = (new String(Arrays.copyOfRange(bytes, 20, 52))).trim();
        UserProfile userProfile = new UserProfile();
        userProfile.setEnabled((bytes[0] >> 0 & 1) != 0);
        userProfile.setPasswordEnabled((bytes[0] >> 1 & 1) != 0);
        userProfile.setMultiLogInEnabled((bytes[0] >> 2 & 1) != 0);
        userProfile.setAcl(bytes[1]);
        userProfile.setUserName(user);
        userProfile.setPassword(pwd);
        return userProfile;
    }

    public byte[] getBytes() {
        byte[] userProfileBytes = new byte[52];
        if (enabled) {
            userProfileBytes[0] = (byte)(userProfileBytes[0] | 1);
        } else {
            userProfileBytes[0] &= -2;
        }

        if (passwordEnabled) {
            userProfileBytes[0] = (byte)(userProfileBytes[0] | 2);
        } else {
            userProfileBytes[0] &= -3;
        }

        if (multiLogInEnabled) {
            userProfileBytes[0] = (byte)(userProfileBytes[0] | 4);
        } else {
            userProfileBytes[0] &= -5;
        }

        userProfileBytes[1] = acl;
        byte[] bUsername = userName.getBytes();
        System.arraycopy(bUsername, 0, userProfileBytes, 4, bUsername.length);

        byte[] bPassword = password.getBytes();
        System.arraycopy(bPassword, 0, userProfileBytes, 20, bPassword.length);

        return userProfileBytes;
    }
}
