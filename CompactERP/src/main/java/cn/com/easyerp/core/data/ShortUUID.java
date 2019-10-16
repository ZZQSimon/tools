package cn.com.easyerp.core.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShortUUID {
    static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z' };

    static final Map<Character, Integer> digitMap = new HashMap<>();
    public static final int MAX_RADIX;

    static {
        for (int i = 0; i < digits.length; i++) {
            digitMap.put(Character.valueOf(digits[i]), Integer.valueOf(i));
        }

        MAX_RADIX = digits.length;
    }

    public static final int MIN_RADIX = 2;

    public static String toString(long i, int radix) {
        if (radix < 2 || radix > MAX_RADIX)
            radix = 10;
        if (radix == 10) {
            return Long.toString(i);
        }
        // int size = 65;
        int charPos = 64;

        char[] buf = new char[65];
        boolean negative = (i < 0L);

        if (!negative) {
            i = -i;
        }

        while (i <= -radix) {
            buf[charPos--] = digits[(int) -(i % radix)];
            i /= radix;
        }
        buf[charPos] = digits[(int) -i];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, 65 - charPos);
    }

    static NumberFormatException forInputString(String s) {
        return new NumberFormatException("For input string: \"" + s + "\"");
    }

    public static long toNumber(String s, int radix) {
        if (s == null) {
            throw new NumberFormatException("null");
        }

        if (radix < 2) {
            throw new NumberFormatException("radix " + radix + " less than Numbers.MIN_RADIX");
        }

        if (radix > MAX_RADIX) {
            throw new NumberFormatException("radix " + radix + " greater than Numbers.MAX_RADIX");
        }

        long result = 0L;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -9223372036854775807L;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    negative = true;
                    limit = (long) Float.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw forInputString(s);
                }
                if (len == 1) {
                    throw forInputString(s);
                }
                i++;
            }
            long multmin = limit / radix;
            while (i < len) {
                Integer digit = (Integer) digitMap.get(Character.valueOf(s.charAt(i++)));
                if (digit == null) {
                    throw forInputString(s);
                }
                if (digit.intValue() < 0) {
                    throw forInputString(s);
                }
                if (result < multmin) {
                    throw forInputString(s);
                }
                result *= radix;
                if (result < limit + digit.intValue()) {
                    throw forInputString(s);
                }
                result -= digit.intValue();
            }
        } else {
            throw forInputString(s);
        }
        return negative ? result : -result;
    }

    private static String digits(long val, int digits) {
        long hi = 1L << digits * 4;

        return toString(hi | val & hi - 1L, MAX_RADIX).substring(1);
    }

    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        StringBuilder sb = new StringBuilder();
        sb.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(digits(uuid.getMostSignificantBits(), 4));
        sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(digits(uuid.getLeastSignificantBits(), 12));
        return sb.toString();
    }
}
