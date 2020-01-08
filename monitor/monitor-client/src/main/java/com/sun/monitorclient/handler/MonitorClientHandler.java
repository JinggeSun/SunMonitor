package com.sun.monitorclient.handler;

import com.alibaba.fastjson.JSON;
import com.sun.monitorclient.MonitorClientApplication;
import com.sun.monitorclient.protocol.TransProtocol;
import com.sun.monitorclient.util.DateUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MonitorClientHandler extends ChannelInboundHandlerAdapter {


    // 心跳间隔
    private static final Integer HEARTBEAT_INTERVAL = 5;

    /**
     * 连接成功
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与服务器建立连接成功：" + DateUtil.getNow());
        //调用心跳机制，发送数据
        scheduleSendHeartBeat(ctx);
    }

    /**
     * 连接失败
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开服务器连接：" + DateUtil.getNow());
        //断开连接后，重新连接
        MonitorClientApplication.intiClient();
    }

    /**
     * 将数据发送给服务器
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void userEventTriggered(ChannelHandlerContext ctx,String msg) throws Exception {
        ctx.channel().writeAndFlush(msg);
    }

    /**
     * 从服务器接受的参数
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接收到服务器的消息：" + msg.toString());
        String mess = msg.toString();
        Map<String,Object> map = TransProtocol.getMsg(Integer.parseInt(mess));
        //将值返回给服务器
        userEventTriggered(ctx, JSON.toJSONString(map).replaceAll("\"","'"));
    }


    /**
     * 心跳方法
     * 定期向服务器发送数据
     * @param ctx
     */
    private void scheduleSendHeartBeat(ChannelHandlerContext ctx){
        ctx.executor().schedule(()->{
            if (ctx.channel().isActive()){
                System.out.println(TransProtocol.getMsg(1));
                Map<String,Object> map = TransProtocol.getMsg(Integer.parseInt("1"));
                //将值返回给服务器
                ctx.channel().writeAndFlush(JSON.toJSONString(map).replaceAll("\"","'"));
                scheduleSendHeartBeat(ctx);
            }
        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }
}
