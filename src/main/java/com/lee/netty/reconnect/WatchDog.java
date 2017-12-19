package com.lee.netty.reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by liqiang on 2017/12/13.
 */
@ChannelHandler.Sharable
public abstract class WatchDog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

    int reconnectCount = 0;
    private Bootstrap bootstrap;
    private int port;
    private String host;
    private Timer timer;

    public WatchDog(Bootstrap bootstrap, String host, int port, Timer timer) {
        this.bootstrap = bootstrap;
        this.host = host;
        this.port = port;
        this.timer = timer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        reconnectCount = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (reconnectCount < 12) {
            reconnectCount++;
            long reconnectTime = reconnectCount << 2;
            timer.newTimeout(this, reconnectTime, TimeUnit.SECONDS);
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        System.out.println("客户端开始重连");
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(newHandlers());
            }
        });
        SocketAddress address = new InetSocketAddress(host, port);
        ChannelFuture future = bootstrap.connect(address);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("重连成功");
                } else {
                    System.out.println("重连失败");
                    future.channel().pipeline().fireChannelInactive();
                }
            }
        });

    }
}
