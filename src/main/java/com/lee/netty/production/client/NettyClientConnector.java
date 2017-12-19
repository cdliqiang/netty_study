package com.lee.netty.production.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * Created by liqiang on 2017/12/18.
 */
public abstract class NettyClientConnector implements ClientConnector {
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private int nWorkers;
    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    public NettyClientConnector() {
        this(PROCESSORS << 1);
    }

    public NettyClientConnector(int nWorkers) {
        this.nWorkers = nWorkers;
    }

    public void init() {
        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup(nWorkers, new DefaultThreadFactory("client worker thread"));
        bootstrap.group(workerGroup);
    }

    @Override
    public Channel connect(String host, int port) {
        return null;
    }

    @Override
    public void shutDown() {
        workerGroup.shutdownGracefully();
    }


    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }
}
