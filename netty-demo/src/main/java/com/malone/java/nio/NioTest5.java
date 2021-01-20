package com.malone.java.nio;

import java.nio.ByteBuffer;

/**
 * 类型化的get和put
 * 方法中表示了获取数据的类型，也就是读取的字节数
 */
public class NioTest5 {
    public static void main(String[] args) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.putInt(12);
        buffer.putLong(44444444L);
        buffer.putChar('你');
        buffer.putShort((short) 2);

        buffer.flip();

        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getShort());
    }
}
