package com.lee.netty.production.client;

import com.lee.netty.production.common.Heartbeats;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liqiang on 2017/12/18.
 */
@ChannelHandler.Sharable
public class ConnectIdleTrigger extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ConnectIdleTrigger.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                logger.info("need send heartbeat");
                ctx.writeAndFlush(Heartbeats.heartbeatContent());
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }
}
