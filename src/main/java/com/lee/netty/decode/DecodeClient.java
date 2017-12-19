package com.lee.netty.decode;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by liqiang on 2017/12/6.
 */
public class DecodeClient {

    String host;
    int port;

    public DecodeClient(String host, int port) {
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
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast(new DecodeClientHandler());
                        }
                    });
            SocketAddress address = new InetSocketAddress(host, port);
            ChannelFuture future = sb.connect(address).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        DecodeClient server = new DecodeClient("127.0.0.1", 9000);
        server.start();
    }
}
