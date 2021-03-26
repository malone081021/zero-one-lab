package com.malone.bit;

public class Test2 {
    public static void main(String[] args) {
        // 1 0 1
        // 0 0 1
        byte b = 5;
    //   System.out.println(b);
       System.out.println(byteToBit(b));
//        b = (byte) (b | (1 << 2));
//        b = (byte) (b & ~(1 << 0));
//       b =  one(b,3);
//       b =  zero(b,2);
      //  System.out.println(b);
        System.out.println(byteToBit((byte) (1 << 3)));
        System.out.println("=============================");
        System.out.println(byteToBit((byte) (b | 1 << 3)));
    }

    public static byte one(byte b, int index) {
        return (byte) (b | (1 << index));
    }

    public static byte zero(byte b, int index) {
        return (byte) (b & ~(1 << index));
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
