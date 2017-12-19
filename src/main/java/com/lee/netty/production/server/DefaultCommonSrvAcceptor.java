package com.lee.netty.production.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by liqiang on 2017/12/14.
 */
public class DefaultCommonSrvAcceptor extends DefaultSrvAcceptor {

    protected ChannelEventListener channelEventListener;

    private AcceptorIdleStateTrigger acceptorIdleStateTrigger = new AcceptorIdleStateTrigger();
    private AckEncoder ackEncoder = new AckEncoder();
    private MessageHandler messageHandler = new MessageHandler();


    public DefaultCommonSrvAcceptor(int port, ChannelEventListener channelEventListener) {
        super(new InetSocketAddress(port));
        this.init();
        this.channelEventListener = channelEventListener;
    }

    public void init() {
        super.init();
        bootstrap().option(ChannelOption.SO_BACKLOG, 32768)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);



    }

    @Override
    protected ChannelFuture bind(SocketAddress localAddress) {
        ServerBootstrap boot = bootstrap();
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
                ch.pipeline().addLast(acceptorIdleStateTrigger);
                ch.pipeline().addLast(new MessageDecoder());
                ch.pipeline().addLast(ackEncoder);
                ch.pipeline().addLast(messageHandler);

//                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 12, 4, 0, 16));
//                ch.pipeline().addLast(new MessageLengthFieldHandler());
            }
        });
        return boot.bind(localAddress);
    }


}
