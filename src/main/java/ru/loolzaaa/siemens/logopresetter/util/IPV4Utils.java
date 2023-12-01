package ru.loolzaaa.siemens.logopresetter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPV4Utils {

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})(/(\\d{1,3})|)");

    private static IPV4Utils fInstance;

    private IPV4Utils() {
    }

    public static IPV4Utils getInstance() {
        if (fInstance == null) {
            fInstance = new IPV4Utils();
        }
        return fInstance;
    }

    public String formatToString(int val) {
        if (val == 0) {
            return "";
        } else {
            int[] octets = new int[4];

            for(int j = 3; j >= 0; --j) {
                octets[j] |= val >>> 8 * (3 - j) & 255;
            }

            StringBuilder str = new StringBuilder();

            for(int i = 0; i < octets.length; ++i) {
                str.append(octets[i]);
                if (i != octets.length - 1) {
                    str.append(".");
                }
            }

            return str.toString();
        }
    }

    public int formatToInt(String ip) {
        if (ip.isEmpty()) {
            return 0;
        } else {
            int ret = 0;
            Matcher matcher = ADDRESS_PATTERN.matcher(ip);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Could not parse [" + ip + "]");
            } else {
                for(int i = 1; i <= 4; ++i) {
                    int n = Integer.parseInt(matcher.group(i));
                    ret |= (n & 255) << 8 * (4 - i);
                }

                return ret;
            }
        }
    }
}
