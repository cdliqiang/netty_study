package com.lee.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DecodeClientHandler extends ChannelInboundHandlerAdapter {

    //固定分割串
    String delimiterStr = "HelloWorld$_HelloWorldHelloWorld$_HelloWorldHelloWorld";
    //固定长度
    String fixLengthStr = "HelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorld";
    //换行符
    String lineBaseStr = "HelloWorldHelloWorldHelloWorld" + "\n" + "HelloWorldHelloWorldHelloWorld" + "\n";

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client active");
        ByteBuf msg = Unpooled.buffer(lineBaseStr.getBytes().length);
        msg.writeBytes(lineBaseStr.getBytes());
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("DecodeClientHandler read Message:" + msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("DecodeClientHandler inActive===========");
        super.channelInactive(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
