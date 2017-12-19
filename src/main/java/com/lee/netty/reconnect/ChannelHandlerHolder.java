package com.lee.netty.reconnect;

import io.netty.channel.ChannelHandler;

/**
 * Created by liqiang on 2017/10/24.
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] newHandlers();
}
