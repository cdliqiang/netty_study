package com.lee.netty.production.server;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * Created by liqiang on 2017/12/14.
 */
public abstract class DefaultSrvAcceptor extends NettySrvAcceptor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSrvAcceptor.class);


    public DefaultSrvAcceptor(SocketAddress localAddress) {
        super(localAddress);
    }

}
