package com.lee.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by liqiang on 2017/12/6.
 */
public class DecodeServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String msgStr = (String) msg;
        System.out.println("server read :" + msgStr);
//        ByteBuf buf = (ByteBuf) msg;
//        if (buf.isReadable()) {
//            byte[] array = new byte[buf.readableBytes()];
//            buf.readBytes(array);
//            System.out.println(new String(array));
//        }

        ByteBuf buf = Unpooled.copiedBuffer(msgStr.getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
