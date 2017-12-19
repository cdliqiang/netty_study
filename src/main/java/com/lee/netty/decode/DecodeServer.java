package com.lee.netty.decode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by liqiang on 2017/12/6.
 */
public class DecodeServer {

    int port;

    public DecodeServer(int port) {
        this.port = port;
    }

    public void start() {
        ServerBootstrap sbs = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            sbs.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //换行符分割
                            ch.pipeline().addLast(new LineBasedFrameDecoder(2048));
                            //固定长度分割
//                            ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            //固定分割符分割
//                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("$_".getBytes())));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new DecodeServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = sbs.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        DecodeServer server = new DecodeServer(9000);
        server.start();
    }
}
