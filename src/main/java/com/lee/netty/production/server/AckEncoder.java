package com.lee.netty.production.server;

import com.lee.netty.production.common.Acknowledge;
import com.lee.netty.production.common.NettyCommonProtocol;
import com.lee.netty.production.serializer.SerializerHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by liqiang on 2017/12/15.
 */
@ChannelHandler.Sharable
public class AckEncoder extends MessageToByteEncoder<Acknowledge> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Acknowledge msg, ByteBuf out) throws Exception {
        byte[] bytes = SerializerHolder.serializerImpl().writeObject(msg);
        out.writeShort(NettyCommonProtocol.MAGIC)
                .writeByte(NettyCommonProtocol.ACK)
                .writeByte(0)
                .writeLong(msg.sequence())
                .writeInt(bytes.length)
                .writeBytes(bytes);
    }
}
