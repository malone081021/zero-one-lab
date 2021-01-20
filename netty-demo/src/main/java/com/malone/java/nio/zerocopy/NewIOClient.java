package com.malone.java.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8899));

        socketChannel.configureBlocking(true);
        String fileName = ""; // 文件路径
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();
        long startTime = System.currentTimeMillis();
        // 将fileChanne 中的内容，写到SocketChannel
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

    }
}
