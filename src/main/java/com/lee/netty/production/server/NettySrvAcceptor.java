package com.lee.netty.production.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * Created by liqiang on 2017/12/14.
 */
public abstract class NettySrvAcceptor implements SrvAcceptor {
    protected ServerBootstrap bootstrap;
    protected SocketAddress localAddress;
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workGroup;
    private static final Logger logger = LoggerFactory.getLogger(NettySrvAcceptor.class);

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private int nWorkers;


    public NettySrvAcceptor(SocketAddress localAddress) {
        this(localAddress, AVAILABLE_PROCESSORS << 2);
    }

    public NettySrvAcceptor(SocketAddress localAddress, int nWorkers) {
        this.localAddress = localAddress;
        this.nWorkers = nWorkers;
    }

    @Override
    public SocketAddress localAddress() {
        return localAddress;
    }

    //netty的元素初始化
    protected void init() {
        ThreadFactory bossFactory = new DefaultThreadFactory("netty.acceptor.boss");
        ThreadFactory workerFactory = new DefaultThreadFactory("netty.acceptor.worker");

        bossGroup = new NioEventLoopGroup(1, bossFactory);
        workGroup = new NioEventLoopGroup(nWorkers, workerFactory);

        //create && group
        bootstrap = new ServerBootstrap().group(bossGroup, workGroup);

    }

    @Override
    public void start() throws InterruptedException {
        this.start(true);
    }

    @Override
    public void shutdownGracefully() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    @Override
    public void start(boolean sync) throws InterruptedException {
        ChannelFuture future = bind(localAddress).sync();
        if (sync) {
            future.channel().closeFuture().sync();
        }
    }

    protected ServerBootstrap bootstrap() {
        return bootstrap;
    }

    protected abstract ChannelFuture bind(SocketAddress localAddress);

}
