package com.malone.java.nio.chat;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 启动通过telnet 测试
 * 客户端断开对服务端的影响,没有处理 注意异常？？
 */
public class NioServer {
    private static Map<String, SocketChannel> clients = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8899));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.forEach(selectionKey -> {
                    final SocketChannel client;
                    try {
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                            client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                            String key = UUID.randomUUID().toString();
                            clients.put(key, client);
                        } else if (selectionKey.isReadable()) {
                            //   SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            client = (SocketChannel) selectionKey.channel();
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);

                            int count = client.read(readBuffer);

                            if (count > 0) {
                                readBuffer.flip();
                                String receiveMessage = String.valueOf(Charset.forName("utf-8").decode(readBuffer).array());
                                System.out.println(client + ": " + receiveMessage);


                                String senderKey = null; // 寻找当前clientChannel对应的key

                                for (Map.Entry<String, SocketChannel> entry : clients.entrySet()) {
                                    if (client == entry.getValue()) {
                                        senderKey = entry.getKey();
                                        break;
                                    }
                                }

                                for (Map.Entry<String, SocketChannel> entry : clients.entrySet()) {
                                    SocketChannel value = entry.getValue();
                                    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

                                    writeBuffer.put((senderKey + ": " + receiveMessage).getBytes());

                                    writeBuffer.flip();

                                    value.write(writeBuffer);
                                }

                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                selectionKeys.clear(); // 非常重要，但是不知道具体为啥

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
