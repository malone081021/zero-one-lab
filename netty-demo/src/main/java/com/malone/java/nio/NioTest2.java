package com.malone.java.nio;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Nio 文件读取
 */
public class NioTest2 {
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("NioTest2.txt");
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        fileChannel.read(byteBuffer);

        byteBuffer.flip();

        while (byteBuffer.remaining() > 0) {
            byte b = byteBuffer.get();
            System.out.println("Character: " + (char) b);
        }
    }

    /**
     * Character: h
     * Character: e
     * Character: l
     * Character: l
     * Character: o
     * Character:  这两个字节是啥
     * Character:
     *
     * Character: n
     * Character: i
     * Character: h
     * Character: a
     * Character: o
     */
}
