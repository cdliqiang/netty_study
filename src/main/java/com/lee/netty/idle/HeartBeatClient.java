package com.lee.netty.idle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

import java.util.concurrent.TimeUnit;


/**
 * Created by liqiang on 2017/10/24.
 */
public class HeartBeatClient {

    public static String host = "127.0.0.1";
    public static int port = 8080;
    protected final HashedWheelTimer timer = new HashedWheelTimer();

    public void connect() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bt = new Bootstrap();
        bt.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
        final ConnectWatchDog dog = new ConnectWatchDog(bt, port, host, timer) {
            @Override
            public ChannelHandler[] newHandlers() {
                return new ChannelHandler[] {
                        this,
                        new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                        new ConnectIdleTrigger(),
                        new StringEncoder(),
                        new StringDecoder()
                };
            }
        };
        ChannelFuture future = null;
        bt.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(dog.newHandlers());
            }
        });
        future = bt.connect(host, port).sync();

    }

    public static void main(String[] args) throws Exception {
        new HeartBeatClient().connect();

    }

}
