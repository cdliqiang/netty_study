package com.lee.netty.production;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by liqiang on 2017/12/19.
 */
@ChannelHandler.Sharable
public abstract class ConnectionWatchDog extends ChannelInboundHandlerAdapter implements ChannelHandlerHolder, TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionWatchDog.class);
    protected Bootstrap bootstrap;
    protected String host;
    protected int port;
    private Timer timer;

    private int reconnectTimes;

    protected ConnectionWatchDog(Bootstrap bootstrap, String host, int port, Timer timer) {
        this.bootstrap = bootstrap;
        this.host = host;
        this.port = port;
        this.timer = timer;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        reconnectTimes = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        long timeout = 0L;
        if (reconnectTimes < 12) {
            timeout = 2 << reconnectTimes;
            reconnectTimes ++;
        }
        timer.newTimeout(this, timeout, TimeUnit.MICROSECONDS);
        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        ChannelFuture future = null;
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(newHandlers());
                }
            });
            future = bootstrap.connect(host, port);
        }
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    LOG.info("重连成功");
                } else {
                    LOG.info("重连失败");
                    future.channel().pipeline().fireChannelInactive();
                }
            }
        });
    }
}
