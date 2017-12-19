package com.lee.netty.reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by liqiang on 2017/12/13.
 */
public class BeatClient {
    public static String host = "127.0.0.1";
    public static int port = 9000;
    protected final HashedWheelTimer timer = new HashedWheelTimer();

    public void connect() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        final Bootstrap bt = new Bootstrap();
        bt.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
        final WatchDog watchDog = new WatchDog(bt, host, port, timer) {
            @Override
            public ChannelHandler[] newHandlers() {
                return new ChannelHandler[]{
                        this,
                        new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                        new ConnectHeartBeatTrigger(),
                        new StringDecoder(),
                        new StringEncoder(),
                };
            }
        };
        ChannelFuture future = null;
        bt.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(watchDog.newHandlers());
            }
        });
        future = bt.connect(host, port).sync();

    }

    public static void main(String[] args) throws Exception {
        new BeatClient().connect();

    }
}
