package com.mvwsolutions.common;

import java.math.BigInteger;
import java.util.UUID;

/**
 * 
 * @author smineyev
 * 
 */
public class GUIDGenerator {

    public static String generateStringGUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateNumericGUID() {
        UUID uuid = UUID.randomUUID();
        String lsb = longToUnsignedString(uuid.getLeastSignificantBits());
        String msb = longToUnsignedString(uuid.getMostSignificantBits());
        String res = msb + prependString(lsb, '0', 20);
//        String res = new BigInteger(uuid.toString().replace("-", ""), 16).toString();
        return res;
    }

    private static String longToUnsignedString(long l) {
        BigInteger b = BigInteger.ZERO;
        for (int i=0; i<63; i++) {
            long mask = 1L << i;
            if ((l & mask) > 0) {
                b = b.setBit(i);
            }
        }
        if (l < 0) {
            b = b.setBit(63);
        }
        return b.toString();
    }

    private static String prependString(String str, char c, int maxSize) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < maxSize) {
            sb.insert(0, c);
        }
        return sb.toString();
    }

}
