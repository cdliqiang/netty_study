package com.lee.netty.codec.lengthFieldBasedFrame.client;

import com.lee.netty.codec.lengthFieldBasedFrame.CustomMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by liqiang on 2017/10/24.
 */
public class CustomMsgEncoder extends MessageToByteEncoder<CustomMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CustomMsg msg, ByteBuf out) throws Exception {
        if (msg == null) {
            throw new Exception("client send msg is null");
        }
        out.writeByte(msg.getFlag());
        out.writeByte(msg.getType());
        byte[] body = msg.getBody().getBytes();
        out.writeInt(body.length);
        out.writeBytes(body);
    }

}
