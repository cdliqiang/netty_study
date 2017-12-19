package com.lee.netty.production.server;

import com.lee.netty.production.common.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.lee.netty.production.serializer.SerializerHolder.serializerImpl;

/**
 * Created by liqiang on 2017/12/15.
 */
@ChannelHandler.Sharable
public class MessageLengthFieldHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        Message ms = serializerImpl().readObject(bytes, Message.class);
        System.out.println(ms);
        ctx.fireChannelRead(ms);
    }

}
