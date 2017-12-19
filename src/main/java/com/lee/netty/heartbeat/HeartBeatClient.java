package com.lee.netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.channels.Channel;
import java.util.concurrent.TimeUnit;


/**
 * Created by liqiang on 2017/10/24.
 */
public class HeartBeatClient {

    public static String host = "127.0.0.1";
    public static int port = 8080;

    public void connect() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bt = new Bootstrap();
        ChannelFuture future = null;
        try {
            bt.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("encode", new StringEncoder());
                            ch.pipeline().addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new HeartBeatClientHandler());
                        }
                    });
            future = bt.connect(host, port).sync();
            future.channel().writeAndFlush("Hello Netty Server ,I am a common client");
            future.channel().closeFuture().sync();
        } finally {
            if (future != null && future.channel().isOpen()) {
                future.channel().close();
            }
            connect();
        }
    }

    public static void main(String[] args) throws Exception {
        new HeartBeatClient().connect();

    }

}
