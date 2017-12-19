package com.lee.netty.idle;

import io.netty.channel.ChannelHandler;

/**
 * Created by liqiang on 2017/10/24.
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] newHandlers();
}
