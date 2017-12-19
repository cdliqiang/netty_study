package com.lee.netty.heartbeat;

import com.lee.netty.codec.lengthFieldBasedFrame.server.CustomMsgDecoder;
import com.lee.netty.codec.lengthFieldBasedFrame.server.CustomServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by liqiang on 2017/10/24.
 */
public class HeartBeatServer {

    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap sbt = new ServerBootstrap();
            sbt.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("encode", new StringEncoder());
                            ch.pipeline().addLast("decode", new StringDecoder());
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new HeartBeatServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
            ;
            ChannelFuture future = sbt.bind(port).sync();
            System.out.println("服务开始在端口：" + port + "上监听请求");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        HeartBeatServer server = new HeartBeatServer();
        server.setPort(port);
        server.start();
    }

}
