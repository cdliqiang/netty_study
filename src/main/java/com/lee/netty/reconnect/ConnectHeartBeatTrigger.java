package com.lee.netty.reconnect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * Created by liqiang on 2017/12/13.
 */
@ChannelHandler.Sharable
public class ConnectHeartBeatTrigger extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEART_BUF = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heart Beat", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(HEART_BUF.duplicate());
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }
}
