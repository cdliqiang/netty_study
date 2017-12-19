package com.lee.netty.production.server;

import com.lee.netty.production.common.Acknowledge;
import com.lee.netty.production.common.Message;
import io.netty.channel.*;

/**
 * Created by liqiang on 2017/12/15.
 */
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        System.out.println(message);
        Channel channel = ctx.channel();
        // 接收到发布信息的时候，要给Client端回复ACK
        channel.writeAndFlush(new Acknowledge(message.sequence())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}