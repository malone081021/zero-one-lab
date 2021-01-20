package com.malone.java.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 */
public class NioTest8 {
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("input2.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("output2.txt");

        FileChannel inputChannel = fileInputStream.getChannel();
        FileChannel outputChannel = fileOutputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocateDirect(512);

        while (true) {
            buffer.clear(); // clear 方法注释，会有什么效果，并尝试解释

            int read = inputChannel.read(buffer);

            System.out.println("read:" + read);

            if (-1 == read) {
                break;
            }

            buffer.flip();
            outputChannel.write(buffer);
        }

        inputChannel.close();
        outputChannel.close();
    }
}
