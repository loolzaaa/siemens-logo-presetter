package ru.loolzaaa.siemens.logopresetter.net;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LogoRequest {

    private final String reqCommand;
    private final int timeout;
    private int length;
    private final HashMap<String, ValueBitLocation> bitLocationMap;
    private final List<Byte> bytesArray;

    private LogoRequest(Builder builder) {
        this.reqCommand = builder.reqCommand;
        this.timeout = builder.timeout;
        this.length = 28;
        this.bytesArray = new ArrayList<>();

        for (int i = 0; i < this.length; ++i) {
            this.bytesArray.add(null);
        }

        this.bitLocationMap = new HashMap<>();
        this.bitLocationMap.put("SIGNATURE", new ValueBitLocation(75, 0, 7));
        this.bitLocationMap.put("VERSION", new ValueBitLocation(0, 8, 11));
        this.bitLocationMap.put("CLIENTVERSION", new ValueBitLocation(0, 25, 28));
        this.bitLocationMap.put("SYNCCALLFLG", new ValueBitLocation(builder.syncCallFlg, 29, 29));
        this.bitLocationMap.put("NEEDREURNFLG", new ValueBitLocation(builder.needReturnFlg, 30, 30));
        this.bitLocationMap.put("REQUESTFLG", new ValueBitLocation(builder.requestFlg, 31, 31));
    }

    private void put(int aByte, int start) {
        byte bVal = (byte)(aByte & 255);
        if (start > bytesArray.size() - 1) {
            bytesArray.add(start, bVal);
        } else {
            bytesArray.set(start, bVal);
        }

    }

    private void putWord(int a16BitValue, int start) {
        byte lowVal = (byte)(a16BitValue & 255);
        byte highVal = (byte)((a16BitValue & '\uff00') >> 8);
        if (start > bytesArray.size() - 1) {
            bytesArray.add(lowVal);
            bytesArray.add(highVal);
        } else {
            bytesArray.set(start, lowVal);
            bytesArray.set(start + 1, highVal);
        }

    }

    private void putDoubleWord(int a32BitValue, int start) {
        putWord(a32BitValue & '\uffff', start);
        putWord((a32BitValue & -65536) >> 16, start + 2);
    }

    byte[] toByteArray() {
        byte[] ret = new byte[bytesArray.size()];
        int i = 0;

        Byte e;
        for(Iterator<Byte> it = bytesArray.iterator(); it.hasNext(); ret[i++] = e) {
            e = it.next();
            if (e == null) {
                throw new RuntimeException("fatal error: byte array contains null value");
            }
        }

        return ret;
    }

    public byte[] getRequestByte() {
        BitSerial bitSerial = new BitSerial(32);
        bitLocationMap.put("LENGTH", new ValueBitLocation(length, 12, 24));

        for (String key : bitLocationMap.keySet()) {
            bitSerial.putValue(bitLocationMap.get(key));
        }

        byte[] ret = bitSerial.parseBitSerial();

        for(int i = 0; i < ret.length; ++i) {
            put(ret[i], i);
        }

        int context = 0;
        putDoubleWord(context, 4);
        int session = 0;
        putDoubleWord(session, 8);

        for(int i = 0; i < 12; ++i) {
            if (reqCommand.length() > i) {
                put(reqCommand.charAt(i), 12 + i);
            } else {
                put(0, 12 + i);
            }
        }

        putDoubleWord(timeout, 24);
        if (length != bytesArray.size()) {
            throw new RuntimeException("fatal error :byte value size not valid");
        } else {
            return toByteArray();
        }
    }

    public void setParameter(int intVal) {
        put(intVal, length);
        ++length;
    }

    public void setParameterIntArray(int[] intArray, int mode) {
        if (intArray != null) {
            for (int j : intArray) {
                if (mode == 0) {
                    setParameter(j);
                } else if (mode == 1) {
                    setWordParameter(j);
                } else if (mode == 2) {
                    setDoubleWordParameter(j);
                }
            }
        }

    }

    public void setWordParameter(int intVal) {
        putWord(intVal, length);
        length += 2;
    }

    public void setDoubleWordParameter(int intVal) {
        putDoubleWord(intVal, length);
        length += 4;
    }

    public void setByteArrayParameter(byte[] byteVal) {
        for(int i = 0; i < byteVal.length; ++i) {
            put(byteVal[i], length + i);
        }

        length += byteVal.length;
    }

    public static void main(String[] args) throws Exception {
    }

    public static class Builder {
        private final String reqCommand;
        private int syncCallFlg = 1;
        private int needReturnFlg = 1;
        private int requestFlg = 1;
        private int timeout = 10000;

        public Builder(String reqCommand) {
            this.reqCommand = reqCommand;
        }

        public Builder syncCallFlg(int val) {
            syncCallFlg = val;
            return this;
        }

        public Builder needReturnFlg(int val) {
            needReturnFlg = val;
            return this;
        }

        public Builder requestFlg(int val) {
            requestFlg = val;
            return this;
        }

        public Builder timeout(int val) {
            timeout = val;
            return this;
        }

        public LogoRequest build() {
            return new LogoRequest(this);
        }
    }

    protected static class BitSerial {
        ArrayList<Byte> list;

        public BitSerial(int len) {
            this.list = new ArrayList<>(len);

            for(int i = 0; i < len; ++i) {
                this.list.add(null);
            }

        }

        public void putValue(ValueBitLocation valBitLoc) {
            for(int i = 0; i <= valBitLoc.getEnd() - valBitLoc.getStart(); ++i) {
                byte bitValue = (byte)(valBitLoc.getValue() >> i & 1);
                if (i > 31) {
                    list.set(valBitLoc.getStart() + i, (byte)0);
                } else {
                    list.set(valBitLoc.getStart() + i, bitValue);
                }
            }

        }

        public byte[] parseBitSerial() {
            if (!list.isEmpty() && list.size() % 8 == 0) {
                int byteLen = list.size() / 8;
                byte[] bytes = new byte[byteLen];

                for(int i = 0; i < byteLen; ++i) {
                    byte singleByte = 0;

                    for(int j = 8; j > 0; --j) {
                        singleByte = (byte)(singleByte << 1);
                        singleByte |= list.get(i * 8 + j - 1);
                    }

                    bytes[i] = singleByte;
                }

                return bytes;
            } else {
                return null;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    protected static class ValueBitLocation {
        private final int value;
        private final int start;
        private final int end;
    }
}
