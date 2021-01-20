package com.malone.netty.demo.secondexample;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {
    public static void main(String[] args) {
        // 客户端只需要一个事件循环
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap Bootstrap = new Bootstrap().group(eventLoopGroup).channel(NioSocketChannel.class).
                    handler(new MyClientInitilaizer());
            ChannelFuture channelFuture = Bootstrap.connect("localhost", 8899).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
