package com.lee.netty.production.client;

import io.netty.channel.Channel;

/**
 * Created by liqiang on 2017/12/18.
 */
public interface ClientConnector {

    public Channel connect(String host, int port);

    public void shutDown();
}
