package com.sun.monitorserver.server;

import com.sun.monitorserver.server.handler.IMIdleStateHandler;
import com.sun.monitorserver.server.handler.MinitorServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author zcm
 */
public class MonitorServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        // 空闲检测
        pipeline.addLast(new IMIdleStateHandler());
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new MinitorServerHandler());
    }
}
