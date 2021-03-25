package com.malone.bit;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

public class Test {
    public static void main(String[] args) {
        // 1 0 1
        // 0 0 1
        byte b = 5;
        System.out.println(b);
        System.out.println(byteToBit(b));
//        b = (byte) (b | (1 << 2));
//        b = (byte) (b & ~(1 << 0));
       b =  one(b,3);
       b =  zero(b,2);
        System.out.println(b);
        System.out.println(byteToBit(b));
        // System.out.println(3 & 1);
//        for (int i = 0; i < 100; i ++) {
//            if ((i & 1) == 0) { // 偶数
//                System.out.println(i);
//            }
//        }
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
