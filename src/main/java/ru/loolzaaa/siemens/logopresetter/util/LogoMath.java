package ru.loolzaaa.siemens.logopresetter.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class LogoMath {

    public static final byte[] keyForBinFile = new byte[]{19, 41, 38, -116, 10, -34, 114, 65};
    private static final int[] g_u32CRC32Table = new int[]{0, 1996959894, -301047508, -1727442502, 124634137, 1886057615, -379345611, -1637575261, 249268274, 2044508324, -522852066, -1747789432, 162941995, 2125561021, -407360249, -1866523247, 498536548, 1789927666, -205950648, -2067906082, 450548861, 1843258603, -187386543, -2083289657, 325883990, 1684777152, -43845254, -1973040660, 335633487, 1661365465, -99664541, -1928851979, 997073096, 1281953886, -715111964, -1570279054, 1006888145, 1258607687, -770865667, -1526024853, 901097722, 1119000684, -608450090, -1396901568, 853044451, 1172266101, -589951537, -1412350631, 651767980, 1373503546, -925412992, -1076862698, 565507253, 1454621731, -809855591, -1195530993, 671266974, 1594198024, -972236366, -1324619484, 795835527, 1483230225, -1050600021, -1234817731, 1994146192, 31158534, -1731059524, -271249366, 1907459465, 112637215, -1614814043, -390540237, 2013776290, 251722036, -1777751922, -519137256, 2137656763, 141376813, -1855689577, -429695999, 1802195444, 476864866, -2056965928, -228458418, 1812370925, 453092731, -2113342271, -183516073, 1706088902, 314042704, -1950435094, -54949764, 1658658271, 366619977, -1932296973, -69972891, 1303535960, 984961486, -1547960204, -725929758, 1256170817, 1037604311, -1529756563, -740887301, 1131014506, 879679996, -1385723834, -631195440, 1141124467, 855842277, -1442165665, -586318647, 1342533948, 654459306, -1106571248, -921952122, 1466479909, 544179635, -1184443383, -832445281, 1591671054, 702138776, -1328506846, -942167884, 1504918807, 783551873, -1212326853, -1061524307, -306674912, -1698712650, 62317068, 1957810842, -355121351, -1647151185, 81470997, 1943803523, -480048366, -1805370492, 225274430, 2053790376, -468791541, -1828061283, 167816743, 2097651377, -267414716, -2029476910, 503444072, 1762050814, -144550051, -2140837941, 426522225, 1852507879, -19653770, -1982649376, 282753626, 1742555852, -105259153, -1900089351, 397917763, 1622183637, -690576408, -1580100738, 953729732, 1340076626, -776247311, -1497606297, 1068828381, 1219638859, -670225446, -1358292148, 906185462, 1090812512, -547295293, -1469587627, 829329135, 1181335161, -882789492, -1134132454, 628085408, 1382605366, -871598187, -1156888829, 570562233, 1426400815, -977650754, -1296233688, 733239954, 1555261956, -1026031705, -1244606671, 752459403, 1541320221, -1687895376, -328994266, 1969922972, 40735498, -1677130071, -351390145, 1913087877, 83908371, -1782625662, -491226604, 2075208622, 213261112, -1831694693, -438977011, 2094854071, 198958881, -2032938284, -237706686, 1759359992, 534414190, -2118248755, -155638181, 1873836001, 414664567, -2012718362, -15766928, 1711684554, 285281116, -1889165569, -127750551, 1634467795, 376229701, -1609899400, -686959890, 1308918612, 956543938, -1486412191, -799009033, 1231636301, 1047427035, -1362007478, -640263460, 1088359270, 936918000, -1447252397, -558129467, 1202900863, 817233897, -1111625188, -893730166, 1404277552, 615818150, -1160759803, -841546093, 1423857449, 601450431, -1285129682, -1000256840, 1567103746, 711928724, -1274298825, -1022587231, 1510334235, 755167117};
    private static final BigInteger[] g_RSAKey = new BigInteger[]{new BigInteger("6E0D55867F39E77B87D8E86E916F3F0270B0A4026B49BB7EDF620AA9736FF84EC1EB77E5134C667B0D432AA55F97968CA46F93D1D9BFFDD898A2B77A8290BBECBE08B99D34E7A57A8FB324F7347D39EF699EAB1DFA88575352933A5BCEF1A09CBA43814D11F85B06505DE6B72486AD5C19DCAB1628F0B02AB5C5066642800629", 16), new BigInteger("25FBB", 16), new BigInteger("131009489")};
    private static final byte[] _st_PCTable1 = new byte[]{56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3};
    private static final byte[] _st_PCTable2 = new byte[]{13, 16, 10, 23, 0, 4, 2, 27, 14, 5, 20, 9, 22, 18, 11, 3, 25, 7, 15, 6, 26, 19, 12, 1, 40, 51, 30, 36, 46, 54, 29, 39, 50, 44, 32, 47, 43, 48, 38, 55, 33, 52, 45, 41, 49, 35, 28, 31};
    private static final byte[] _st_LeftShiftTable = new byte[]{1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    private static final byte[] _st_IPTable = new byte[]{57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7, 56, 48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6};
    private static final byte[] _st_IIPTable = new byte[]{39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25, 32, 0, 40, 8, 48, 16, 56, 24};
    private static final byte[] _st_EPTable = new byte[]{31, 0, 1, 2, 3, 4, 3, 4, 5, 6, 7, 8, 7, 8, 9, 10, 11, 12, 11, 12, 13, 14, 15, 16, 15, 16, 17, 18, 19, 20, 19, 20, 21, 22, 23, 24, 23, 24, 25, 26, 27, 28, 27, 28, 29, 30, 31, 0};
    private static final byte[] _st_PPTable = new byte[]{15, 6, 19, 20, 28, 11, 27, 16, 0, 14, 22, 25, 4, 17, 30, 9, 1, 7, 23, 13, 31, 26, 2, 8, 18, 12, 29, 5, 21, 10, 3, 24};
    private static final byte[] _st_SandBox1 = new byte[]{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13};
    private static final byte[] _st_SandBox2 = new byte[]{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9};
    private static final byte[] _st_SandBox3 = new byte[]{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12};
    private static final byte[] _st_SandBox4 = new byte[]{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14};
    private static final byte[] _st_SandBox5 = new byte[]{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3};
    private static final byte[] _st_SandBox6 = new byte[]{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13};
    private static final byte[] _st_SandBox7 = new byte[]{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12};
    private static final byte[] _st_SandBox8 = new byte[]{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11};
    public static char[] G64_Table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

    public static long parseString2Long(String strValue) {
        return Long.parseLong(strValue);
    }

    public static long getHashValue(String strValue) {
        byte[] bytesOfString = strValue.getBytes();
        return createCRC32(bytesOfString);
    }

    private static long createCRC32(byte[] originalBytes) {
        long u32CRC = 4294967295L;

        for (byte originalByte : originalBytes) {
            int location = (int) (u32CRC & 255L ^ (long) originalByte);
            u32CRC = u32CRC >> 8 ^ 4294967295L & (long) g_u32CRC32Table[location];
        }

        return 4294967295L - u32CRC;
    }

    private static BigInteger rsaCalc(BigInteger originalBigInt, BigInteger rsaKey, BigInteger rsaMod) {
        BigInteger bigResult = new BigInteger("1");
        BigInteger bigTemp = originalBigInt.remainder(rsaMod);

        for(BigInteger big_two = new BigInteger("2"); rsaKey.longValue() != 0L; rsaKey = rsaKey.divide(big_two)) {
            if (rsaKey.remainder(big_two).longValue() != 0L) {
                bigResult = bigResult.multiply(bigTemp).remainder(rsaMod);
            }

            bigTemp = bigTemp.multiply(bigTemp).remainder(rsaMod);
        }

        return bigResult;
    }

    private static void bytesToBits(byte[] bitsArray, byte[] bytesArray) {
        int u8Mask = 128;

        for(int index = 0; index < bitsArray.length && index / 8 != bytesArray.length; ++index) {
            int temp = bytesArray[index / 8] & u8Mask >> index % 8;
            bitsArray[index] = (byte)(temp >> 7 - index % 8);
        }

    }

    private static void bitsToBytes(byte[] pBytesArray, byte[] pBitsArray) {
        for(int index = 0; index < pBitsArray.length && index / 8 != pBytesArray.length; ++index) {
            if (index % 8 == 0) {
                pBytesArray[index / 8] = 0;
            }

            byte temp = (byte)(pBitsArray[index] << 7 - index % 8);
            pBytesArray[index / 8] |= temp;
        }

    }

    private static void desKey56LeftShift1(byte[] pKey56) {
        byte u8Temp0 = pKey56[0];

        for(int index = 0; index < 55; ++index) {
            pKey56[index] = pKey56[index + 1];
        }

        pKey56[55] = pKey56[27];
        pKey56[27] = u8Temp0;
    }

    private static void desKey56LeftShift2(byte[] pKey56) {
        byte u8Temp0 = pKey56[0];
        byte u8Temp1 = pKey56[1];

        for(int index = 0; index < 54; ++index) {
            pKey56[index] = pKey56[index + 2];
        }

        pKey56[54] = pKey56[26];
        pKey56[55] = pKey56[27];
        pKey56[26] = u8Temp0;
        pKey56[27] = u8Temp1;
    }

    private static void desInitializeKey(byte[] pKey, byte[][] au8SubKeyGroup) {
        byte[] au8KeyBytesArray64 = new byte[64];
        bytesToBits(au8KeyBytesArray64, pKey);
        byte[] au8KeyBytesArray56 = new byte[56];

        byte u8Index;
        for(u8Index = 0; u8Index < 56; ++u8Index) {
            au8KeyBytesArray56[u8Index] = au8KeyBytesArray64[_st_PCTable1[u8Index]];
        }

        for(u8Index = 0; u8Index < 16; ++u8Index) {
            if (1 == _st_LeftShiftTable[u8Index]) {
                desKey56LeftShift1(au8KeyBytesArray56);
            } else {
                desKey56LeftShift2(au8KeyBytesArray56);
            }

            for(byte u8SubKeyIndex = 0; u8SubKeyIndex < 48; ++u8SubKeyIndex) {
                au8SubKeyGroup[u8Index][u8SubKeyIndex] = au8KeyBytesArray56[_st_PCTable2[u8SubKeyIndex]];
            }
        }

    }

    private static void desCalc(byte[] pData, byte[][] au8SubKeyGroup, boolean bEncrypt) {
        byte[] au8DataBytesArray64 = new byte[64];
        bytesToBits(au8DataBytesArray64, pData);
        byte[] au8DataBytesArrayIP = new byte[64];

        byte u8Index;
        for(u8Index = 0; u8Index < au8DataBytesArrayIP.length; ++u8Index) {
            au8DataBytesArrayIP[u8Index] = au8DataBytesArray64[_st_IPTable[u8Index]];
        }

        byte[] lPart = new byte[32];
        byte[] rPart = new byte[32];

        for(u8Index = 0; u8Index < lPart.length; ++u8Index) {
            lPart[u8Index] = au8DataBytesArrayIP[u8Index];
            rPart[u8Index] = au8DataBytesArrayIP[u8Index + 32];
        }

        if (bEncrypt) {
            for(u8Index = 0; u8Index < 16; ++u8Index) {
                desCalcRound(lPart, rPart, au8SubKeyGroup[u8Index]);
            }
        } else {
            for(u8Index = 15; u8Index >= 0; --u8Index) {
                desCalcRound(lPart, rPart, au8SubKeyGroup[u8Index]);
            }
        }

        for(u8Index = 0; u8Index < lPart.length; ++u8Index) {
            au8DataBytesArrayIP[u8Index] = rPart[u8Index];
            au8DataBytesArrayIP[u8Index + 32] = lPart[u8Index];
        }

        for(u8Index = 0; u8Index < au8DataBytesArrayIP.length; ++u8Index) {
            au8DataBytesArray64[u8Index] = au8DataBytesArrayIP[_st_IIPTable[u8Index]];
        }

        bitsToBytes(pData, au8DataBytesArray64);
    }

    private static void desCalcRound(byte[] pu8LPart, byte[] pu8RPart, byte[] pu8Key) {
        byte[] au8TempLPart = new byte[32];

        byte u8Index;
        for(u8Index = 0; u8Index < au8TempLPart.length; ++u8Index) {
            au8TempLPart[u8Index] = pu8LPart[u8Index];
            pu8LPart[u8Index] = pu8RPart[u8Index];
        }

        byte[] au8EPermBytesArray = new byte[48];

        for(u8Index = 0; u8Index < au8EPermBytesArray.length; ++u8Index) {
            au8EPermBytesArray[u8Index] = pu8RPart[_st_EPTable[u8Index]];
        }

        for(u8Index = 0; u8Index < au8EPermBytesArray.length; ++u8Index) {
            au8EPermBytesArray[u8Index] ^= pu8Key[u8Index];
        }

        byte[] au8SandOutput = new byte[32];
        DESSandBoxFiltHelper(au8SandOutput, 0, 4, DESSandBoxSearchHelper(_st_SandBox1, 0, au8EPermBytesArray));
        DESSandBoxFiltHelper(au8SandOutput, 4, 4, DESSandBoxSearchHelper(_st_SandBox2, 6, au8EPermBytesArray));
        DESSandBoxFiltHelper(au8SandOutput, 8, 4, DESSandBoxSearchHelper(_st_SandBox3, 12, au8EPermBytesArray));
        DESSandBoxFiltHelper(au8SandOutput, 12, 4, DESSandBoxSearchHelper(_st_SandBox4, 18, au8EPermBytesArray));
        DESSandBoxFiltHelper(au8SandOutput, 16, 4, DESSandBoxSearchHelper(_st_SandBox5, 24, au8EPermBytesArray));
        DESSandBoxFiltHelper(au8SandOutput, 20, 4, DESSandBoxSearchHelper(_st_SandBox6, 30, au8EPermBytesArray));
        DESSandBoxFiltHelper(au8SandOutput, 24, 4, DESSandBoxSearchHelper(_st_SandBox7, 36, au8EPermBytesArray));
        DESSandBoxFiltHelper(au8SandOutput, 28, 4, DESSandBoxSearchHelper(_st_SandBox8, 42, au8EPermBytesArray));

        for(u8Index = 0; u8Index < au8SandOutput.length; ++u8Index) {
            pu8RPart[u8Index] = au8SandOutput[_st_PPTable[u8Index]];
        }

        for(u8Index = 0; u8Index < au8SandOutput.length; ++u8Index) {
            pu8RPart[u8Index] ^= au8TempLPart[u8Index];
        }

    }

    private static void DESSandBoxFiltHelper(byte[] pu8BytesArray, int u8StartIndex, int u8Count, byte u8Value) {
        for(int u8Index = u8Count - 1; u8Index >= 0; --u8Index) {
            pu8BytesArray[u8StartIndex + u8Index] = (byte)(u8Value & 1);
            u8Value = (byte)(u8Value >> 1);
        }

    }

    private static byte DESSandBoxSearchHelper(byte[] pu8SandBox, int u8Index, byte[] pBitsArray) {
        int u8Result = pBitsArray[u8Index] << 5 | pBitsArray[u8Index + 5] << 4 | pBitsArray[u8Index + 1] << 3 | pBitsArray[u8Index + 2] << 2 | pBitsArray[u8Index + 3] << 1 | pBitsArray[u8Index + 4];
        return pu8SandBox[u8Result];
    }

    public static String base16_encode(byte[] bytes) {
        char[] temp = new char[bytes.length * 2];

        for(int i = 0; i < bytes.length; ++i) {
            char val = (char)((bytes[i] & 240) >> 4 & 15);
            temp[i * 2] = (char)(val > '\t' ? val + 65 - 10 : val + 48);
            val = (char)(bytes[i] & 15);
            temp[i * 2 + 1] = (char)(val > '\t' ? val + 65 - 10 : val + 48);
        }

        return new String(temp);
    }

    public static byte[] base16_decode(String str) {
        byte[] decodeBytes = new byte[str.length() / 2];

        for(int i = 0; i < str.length() / 2; ++i) {
            char temp = str.charAt(2 * i);
            if (temp >= '0' && temp <= '9') {
                decodeBytes[i] = (byte)(temp - 48);
            } else if (temp >= 'A' && temp <= 'F') {
                decodeBytes[i] = (byte)(temp - 65 + 10);
            }

            decodeBytes[i] = (byte)(decodeBytes[i] << 4);
            temp = str.charAt(2 * i + 1);
            if (temp >= '0' && temp <= '9') {
                decodeBytes[i] |= (byte)(temp - 48);
            } else if (temp >= 'A' && temp <= 'F') {
                decodeBytes[i] |= (byte)(temp - 65 + 10);
            }
        }

        return decodeBytes;
    }

    public static String base64_encode(byte[] bytes) {
        int u32SrcIndex = 0;
        StringBuilder sb = new StringBuilder();

        while(u32SrcIndex < bytes.length) {
            int i32Index = 0;
            int i32TransformUnitBytes = 1;
            int u32TransformUnit = 0;

            while(true) {
                ++i32Index;
                if (i32Index > 3) {
                    for(i32Index = 18; i32Index >= 0; i32Index -= 6) {
                        --i32TransformUnitBytes;
                        int u8TransformPos;
                        if (i32TransformUnitBytes >= 0) {
                            u8TransformPos = u32TransformUnit >>> i32Index & 63;
                        } else {
                            u8TransformPos = 64;
                        }

                        sb.append(G64_Table[u8TransformPos]);
                    }
                    break;
                }

                u32TransformUnit <<= 8;
                if (u32SrcIndex < bytes.length) {
                    u32TransformUnit |= bytes[u32SrcIndex++] & 255;
                    ++i32TransformUnitBytes;
                }
            }
        }

        return sb.toString();
    }

    public static byte[] base64_decode(String str) {
        String G64str = new String(G64_Table);
        int u32SrcIndex = 0;
        ArrayList<Byte> oUINT8Array = new ArrayList<>();

        while(u32SrcIndex + 3 < str.length()) {
            int i32Index = 0;
            int i32TransformUnitBytes = 0;
            int u32TransformUnit = 0;

            while(true) {
                ++i32Index;
                if (i32Index > 4) {
                    for(i32Index = 16; i32Index >= 0; i32Index -= 8) {
                        --i32TransformUnitBytes;
                        if (i32TransformUnitBytes > 0) {
                            oUINT8Array.add((byte)(u32TransformUnit >> i32Index & 255));
                        }
                    }
                    break;
                }

                u32TransformUnit <<= 6;
                if (str.charAt(u32SrcIndex) != G64_Table[64]) {
                    u32TransformUnit |= G64str.indexOf(str.charAt(u32SrcIndex)) & 63;
                    ++u32SrcIndex;
                    ++i32TransformUnitBytes;
                }
            }
        }

        byte[] retBytes = new byte[oUINT8Array.size()];

        for(int i = 0; i < oUINT8Array.size(); ++i) {
            retBytes[i] = (Byte)oUINT8Array.get(i);
        }

        return retBytes;
    }

    public static byte[] asyEncrypt(byte[] originalBytes) {
        new BigInteger("0");
        byte[] reversedBytes = new byte[originalBytes.length];

        for(int i = 0; i < originalBytes.length; ++i) {
            reversedBytes[originalBytes.length - 1 - i] = originalBytes[i];
        }

        BigInteger assBigInt = new BigInteger(1, reversedBytes);
        new BigInteger("0");
        BigInteger encryptedValue = rsaCalc(assBigInt, g_RSAKey[1], g_RSAKey[0]);
        byte[] bigEndianBytes = encryptedValue.toByteArray();
        int retLength = bigEndianBytes.length;
        if (bigEndianBytes[0] == 0) {
            --retLength;
        }

        byte[] littleEndianBytes = new byte[retLength];

        for(int i = 0; i < retLength; ++i) {
            littleEndianBytes[i] = bigEndianBytes[bigEndianBytes.length - 1 - i];
        }

        return littleEndianBytes;
    }

    public static byte[] asyDecrypt(byte[] originalBytes) {
        new BigInteger("0");
        byte[] reversedBytes = new byte[originalBytes.length];

        for(int i = 0; i < originalBytes.length; ++i) {
            reversedBytes[originalBytes.length - 1 - i] = originalBytes[i];
        }

        BigInteger assBigInt = new BigInteger(1, reversedBytes);
        new BigInteger("0");
        BigInteger decryptedValue = rsaCalc(assBigInt, g_RSAKey[2], g_RSAKey[0]);
        byte[] bigEndianBytes = decryptedValue.toByteArray();
        int retLength = bigEndianBytes.length;
        if (bigEndianBytes[0] == 0) {
            --retLength;
        }

        byte[] littleEndianBytes = new byte[retLength];

        for(int i = 0; i < retLength; ++i) {
            littleEndianBytes[i] = bigEndianBytes[bigEndianBytes.length - 1 - i];
        }

        return littleEndianBytes;
    }

    public static byte[] symEncrypt(byte[] originalBytes, byte[] keys) {
        byte[][] oSubKeyGroup = new byte[16][48];
        desInitializeKey(keys, oSubKeyGroup);
        byte[] oDataSegment = new byte[8];
        byte[] retBytes = new byte[(originalBytes.length + 7) / 8 * 8];

        for(int index = 0; index < (originalBytes.length + 7) / 8; ++index) {
            int innerIndex;
            for(innerIndex = 0; innerIndex < 8; ++innerIndex) {
                if (index * 8 + innerIndex < originalBytes.length) {
                    oDataSegment[innerIndex] = originalBytes[index * 8 + innerIndex];
                } else {
                    oDataSegment[innerIndex] = 0;
                }
            }

            desCalc(oDataSegment, oSubKeyGroup, true);

            for(innerIndex = 0; innerIndex < 8; ++innerIndex) {
                retBytes[index * 8 + innerIndex] = oDataSegment[innerIndex];
            }
        }

        return retBytes;
    }

    public static byte[] symDecrypt(byte[] encryptedBytes, byte[] keys) {
        if (encryptedBytes.length % 8 != 0) {
            return encryptedBytes;
        } else {
            byte[][] oSubKeyGroup = new byte[16][48];
            desInitializeKey(keys, oSubKeyGroup);
            byte[] oDataSegment = new byte[8];
            byte[] retBytes = new byte[encryptedBytes.length];

            for(int index = 0; index < encryptedBytes.length / 8; ++index) {
                int innerIndex;
                for(innerIndex = 0; innerIndex < 8; ++innerIndex) {
                    oDataSegment[innerIndex] = encryptedBytes[index * 8 + innerIndex];
                }

                desCalc(oDataSegment, oSubKeyGroup, false);

                for(innerIndex = 0; innerIndex < 8; ++innerIndex) {
                    retBytes[index * 8 + innerIndex] = oDataSegment[innerIndex];
                }
            }

            return retBytes;
        }
    }

    public static byte[] triSymEncrypt(byte[] encryptedBytes, byte[] keys1, byte[] keys2) {
        byte[] retBytes = symEncrypt(encryptedBytes, keys1);
        retBytes = symDecrypt(retBytes, keys2);
        retBytes = symEncrypt(retBytes, keys1);
        return retBytes;
    }

    public static byte[] triSymDecrypt(byte[] encryptedBytes, byte[] keys1, byte[] keys2) {
        byte[] retBytes = symDecrypt(encryptedBytes, keys1);
        retBytes = symEncrypt(retBytes, keys2);
        retBytes = symDecrypt(retBytes, keys1);
        return retBytes;
    }

    public static byte[] delRearNilElement(byte[] originalBytes) {
        int realLength = 0;

        for(int i = 0; i < originalBytes.length; ++i) {
            if (originalBytes[i] == 0) {
                realLength = i;
                break;
            }

            ++realLength;
        }

        byte[] noNilBytes;
        noNilBytes = Arrays.copyOfRange(originalBytes, 0, realLength);
        return noNilBytes;
    }
}
