package com.sun.monitorclient;

import com.sun.monitorclient.handler.MonitorClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 客户端主启动类
 * @author zcm
 */
public class MonitorClientApplication {

    /**
     * 服务器参数
     */
    public static String SERVER_HOST_NAME = "127.0.0.1";
    public static Integer SERVER_HOST_PORT = 9091;
    /**
     *  重试次数
     */
    private static final Integer MAX_RETRY_COUNT = 8;

    private static Bootstrap bootstrap;

    public static void main(String[] args) {

        try {
            SERVER_HOST_NAME = args[0];
            SERVER_HOST_PORT = Integer.valueOf(args[1]);
        } catch (Exception e) {
            System.out.println("参数配置错误！！！");
            //return;
        }

        intiClient();

    }

    public static void intiClient(){
        bootstrap = getBootstrap(SERVER_HOST_NAME,SERVER_HOST_PORT);
        connect(bootstrap,SERVER_HOST_NAME,SERVER_HOST_PORT,MAX_RETRY_COUNT);
    }


    private static Bootstrap getBootstrap(String host, Integer port){

        if (bootstrap != null){
            return bootstrap;
        }

        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                // 指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 绑定自定义属性到 channel
                .attr(AttributeKey.newInstance("clientName"), "nettyClient")
                // 设置TCP底层属性
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // todo 心跳查看
                        //检测连接有效性（心跳）,此处功能：5秒内write()未被调用则触发一次useEventTrigger()方法
                        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        //序列化
                        ch.pipeline().addLast("encoder", new StringEncoder());
                        ch.pipeline().addLast("decoder", new StringDecoder());
                        ch.pipeline().addLast(new MonitorClientHandler());
                    }
                });

        return bootstrap;
    }

    /**
     * 当连接服务器失败时，重新进行连接
     * @param bootstrap
     * @param host
     * @param port
     * @param retry
     */
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {

        bootstrap.connect(host, port).addListener(future1 -> {
            if (future1.isSuccess()) {
                System.out.println("连接成功!");
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY_COUNT - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");

                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }
}
