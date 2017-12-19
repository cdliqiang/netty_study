package com.lee.netty.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by liqiang on 2017/12/6.
 */
public class HelloWorldClient {

    String host;
    int port;

    public HelloWorldClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        Bootstrap sb = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            sb.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(new HelloWorldClientHandler2());
                        }
                    });
            SocketAddress address = new InetSocketAddress(host, port);
            ChannelFuture future = sb.connect(address).sync();
            future.channel().writeAndFlush("hello world");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        HelloWorldClient server = new HelloWorldClient("127.0.0.1", 9000);
        server.start();
    }
}
