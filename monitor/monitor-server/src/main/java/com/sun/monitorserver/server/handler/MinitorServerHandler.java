package com.sun.monitorserver.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.sun.monitorserver.socket.SocketServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端核心方法
 * @author zcm
 */
public class MinitorServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 线程安全，保存客户端信息
     * key：机器channel，value：机器ip
     */
    private static Map<Channel, Object> clientMap = new ConcurrentHashMap<>();

    public static Map<String,Object> allMsgMap = new ConcurrentHashMap<>(10);

    /**
     * 连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("有新的客户端添加进来了" + channel);
        //获取成功后，向客户端请求信息
        System.out.println("请求客户端基本数据");
        sendMsg(channel,"1");
        //将客户端channel放到map中
        addChannel(channel,"");
    }

    /**
     * 断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有客户端离开" + ctx.channel());
        removeChannel(ctx.channel());
    }


    /**
     * 读客户端发送过来的消息
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

        System.out.println("接收到客户端发送的数据：" + msg);

        if (StringUtils.isEmpty(msg)){
            return;
        }

        //客户端发送过来的是json字符串，将其转换未map
        JSONObject msgObject = JSONObject.parseObject(msg);

        /*
         * 将ip地址添加到channel的map中
         * 如果存在，不添加
         */
        if (msgObject.containsKey("ipaddr")){
            String ip = msgObject.getString("ipaddr");
            Channel channel = channelHandlerContext.channel();
            if (clientMap.get(channel).toString().length() == 0){
                addChannel(channel,ip);
            }
        }

        /**
         * 将消息添加到公共变量里面
         */
        if (msgObject.containsKey("info")){
            String ip = msgObject.getString("ip");
            allMsgMap.put(ip,msgObject);
            //数据已经更新，通知websocket
            SocketServer.sendAllMsg();
        }

    }

    /**
     * 发送数据
     * @param channel
     * @param msg
     */
    public static void sendMsg(Channel channel,String msg){
        System.out.println("发送数据");
        channel.writeAndFlush(msg);
    }


    /**
     * 加入1台客户端
     * @param channel
     * @param ip
     */
    public void addChannel(Channel channel,String ip){
        clientMap.put(channel,ip);
    }

    /**
     * 断开1台客户端
     * @param channel
     */
    public void removeChannel(Channel channel){
        clientMap.remove(channel);
    }

}
