package com.lee.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by liqiang on 2017/10/24.
 */
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    int beatCount = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg != null) {
            String str = (String) msg;
            System.out.println(str);
        }
        beatCount = 0;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server inactive");
        ctx.fireChannelInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                beatCount ++;
                if (beatCount > 2) {
                    System.out.println("客户端已超过2次没有发送数据，关闭该客户端连接");
                    ctx.channel().close();
                } else {
                    System.out.println("客户端已经5秒没有发送心跳");
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
