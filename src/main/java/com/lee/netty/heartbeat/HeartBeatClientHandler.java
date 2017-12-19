package com.lee.netty.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * Created by liqiang on 2017/10/24.
 */
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
            CharsetUtil.UTF_8));

    int count = 0;
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = "Hello Server, I am a msg";
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client inactive");
        ctx.fireChannelInactive();
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                if (count < 3) {
                    count ++;
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                }
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

}
