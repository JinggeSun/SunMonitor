package com.sun.monitorserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * 监听服务端
 * @author zcm
 */
public class MonitorServer {

    public void run() throws Exception{
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9091);
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MonitorServerInitializer());
            ChannelFuture future = serverBootstrap.bind(address).sync();
            future.channel().closeFuture().sync();
        }finally{
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new MonitorServer().run();
    }
}
