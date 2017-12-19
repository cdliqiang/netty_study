package com.lee.netty.production;

import io.netty.channel.ChannelHandler;

/**
 * Created by liqiang on 2017/12/19.
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] newHandlers();
}
