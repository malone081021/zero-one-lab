package com.malone.bit;

public class Test3 {
    public static void main(String[] args) {
        byte b = 0x09;
        byte c = (byte) 0xff;
        System.out.println(b);
        System.out.println(byteToBit(b));

        System.out.println(c);
        System.out.println(byteToBit(c));

    }
    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }
}
