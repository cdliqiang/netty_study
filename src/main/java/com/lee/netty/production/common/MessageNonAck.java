package com.lee.netty.production.common;

import io.netty.channel.Channel;

/**
 * Created by liqiang on 2017/12/18.
 */
public class MessageNonAck {
    private final long id;

    private final Message msg;
    private final Channel channel;
    private final long timestamp = System.currentTimeMillis();

    public MessageNonAck(Message msg, Channel channel) {
        this.msg = msg;
        this.channel = channel;

        id = msg.sequence();
    }

    public long getId() {
        return id;
    }

    public Message getMsg() {
        return msg;
    }

    public Channel getChannel() {
        return channel;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
