package ru.loolzaaa.siemens.logopresetter.net;

import lombok.Getter;

@Getter
public class LogoResponse {

    private final char signature;
    private final int version;
    private final int length;
    private final int clientVersion;
    private final int syncCallFlg;
    private final int needReturnFlg;
    private final int requestFlg;
    private final int clientContext;
    private final int sessionId;
    private final int result;
    private final byte[] parameter;

    public LogoResponse(byte[] responseBytes, boolean needParseParameter) {
        int header = reverseByte(responseBytes[3]) & 255;
        header |= (reverseByte(responseBytes[2]) & 255) << 8;
        header |= (reverseByte(responseBytes[1]) & 255) << 16;
        header |= (reverseByte(responseBytes[0]) & 255) << 24;
        this.signature = (char)getBit(header, 0, 7);
        this.version = getBit(header, 8, 11);
        this.length = getBit(header, 12, 24);
        this.clientVersion = getBit(header, 25, 28);
        this.syncCallFlg = getBit(header, 29);
        this.needReturnFlg = getBit(header, 30);
        this.requestFlg = getBit(header, 31);
        this.clientContext = getInt(responseBytes, 4);
        this.sessionId = getInt(responseBytes, 8);
        this.result = getInt(responseBytes, 12);
        if (this.length > 16 && needParseParameter) {
            this.parameter = new byte[this.length - 16];
            System.arraycopy(responseBytes, 16, this.parameter, 0, this.parameter.length);
        } else {
            this.parameter = new byte[0];
        }

    }

    private int getBit(int val, int bitNum) {
        return val >> 32 - (bitNum + 1) & 1;
    }

    private int getBit(int val, int start, int end) {
        int b = 0;

        for(int len = end - start + 1; len > 0; --end) {
            b <<= 1;
            b |= val >> 32 - (end + 1) & 1;
            --len;
        }

        return b;
    }

    private int getInt(byte[] response, int start) {
        int ret = response[start++] & 255;
        ret |= (response[start++] & 255) << 8;
        ret |= (response[start++] & 255) << 16;
        ret |= (response[start++] & 255) << 24;
        return ret;
    }

    private byte reverseByte(byte x) {
        byte t = x;
        byte b = 0;

        for(int len = 8; len > 0; --len) {
            b = (byte)(b << 1);
            b = (byte)(b | t & 1);
            t = (byte)(t >> 1);
        }

        return b;
    }

    public int[] getParameterByteArray() {
        int paraLength = getParameter().length;
        if (paraLength > 0) {
            int[] ret = new int[paraLength];

            for(int i = 0; i < paraLength; ++i) {
                ret[i] = getParameter()[i];
                if (ret[i] < 0) {
                    ret[i] += 256;
                }
            }

            return ret;
        } else {
            return null;
        }
    }

    public int getIntFromParameter(int start) {
        if (start > parameter.length - 4) {
            throw new IllegalArgumentException();
        } else {
            int ret = parameter[start++] & 255;
            ret |= (parameter[start++] & 255) << 8;
            ret |= (parameter[start++] & 255) << 16;
            ret |= (parameter[start++] & 255) << 24;
            return ret;
        }
    }

    public int getUInt8FromParameter(int start) {
        if (start > parameter.length - 1) {
            throw new IllegalArgumentException();
        } else {
            return parameter[start++] & 255;
        }
    }

    public int getUInt16FromParameter(int start) {
        if (start > parameter.length - 2) {
            throw new IllegalArgumentException();
        } else {
            int ret = parameter[start++] & 255;
            ret |= (parameter[start++] & 255) << 8;
            return ret;
        }
    }

    public long getLongFromParameter(int start) {
        if (start > parameter.length - 4) {
            throw new IllegalArgumentException();
        } else {
            long s;
            long s0 = parameter[start++] & 255;
            long s1 = parameter[start++] & 255;
            long s2 = parameter[start++] & 255;
            long s3 = parameter[start++] & 255;
            long s4 = 0L;
            long s5 = 0L;
            long s6 = 0L;
            long s7 = 0L;
            s1 <<= 8;
            s2 <<= 16;
            s3 <<= 24;
            s4 <<= 32;
            s5 <<= 40;
            s6 <<= 48;
            s7 <<= 56;
            s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
            return s;
        }
    }

    public byte getByteFromParameter(int start) {
        if (start > parameter.length - 1) {
            throw new IllegalArgumentException();
        } else {
            return parameter[start];
        }
    }
}
