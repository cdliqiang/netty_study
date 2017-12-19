package com.lee.netty.idle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by liqiang on 2017/10/24.
 */
@ChannelHandler.Sharable
public abstract class ConnectWatchDog extends ChannelInboundHandlerAdapter implements ChannelHandlerHolder, TimerTask {


    Bootstrap bootstrap;
    int port;
    String host;
    Timer timer;
    int reconnectCount = 0;

    public ConnectWatchDog(Bootstrap bootstrap, int port, String host, Timer timer) {
        this.bootstrap = bootstrap;
        this.port = port;
        this.host = host;
        this.timer = timer;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端重新连接成功");
        reconnectCount = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (reconnectCount < 12) {
            reconnectCount++;
            long timeout = 2 << reconnectCount;
            timer.newTimeout(this, timeout, TimeUnit.SECONDS);
        }
        ctx.fireChannelInactive();
    }


    public void run(Timeout timeout) throws Exception {
        System.out.println("开始重连");
        ChannelFuture future;
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(newHandlers());
                }
            });
            future = bootstrap.connect(host, port);
        }

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    System.out.println("重连失败");
                    future.channel().pipeline().fireChannelInactive();
                } else {
                    System.out.println("重连成功");
                }
            }
        });

    }

}
