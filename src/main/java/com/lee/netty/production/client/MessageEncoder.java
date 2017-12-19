package com.lee.netty.production.client;

import com.lee.netty.production.common.Message;
import com.lee.netty.production.common.NettyCommonProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.lee.netty.production.serializer.SerializerHolder.serializerImpl;

/**
 * Created by liqiang on 2017/12/15.
 */
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<Message> {

    /**
     * **************************************************************************************************
     * Protocol
     * ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
     * 2   │   1   │    1   │     8     │      4      │
     * ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤
     * │       │        │           │             │
     * │  MAGIC   Sign    Status   Invoke Id   Body Length                   Body Content              │
     * │       │        │           │             │
     * └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
     * <p/>
     * 消息头16个字节定长
     * = 2 // MAGIC = (short) 0xbabe
     * + 1 // 消息标志位, 用来表示消息类型
     * + 1 // 空
     * + 8 // 消息 id long 类型
     * + 4 // 消息体body长度, int类型
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] bytes = serializerImpl().writeObject(msg);
        out.writeShort(NettyCommonProtocol.MAGIC)
                .writeByte(msg.sign())
                .writeByte(0)
                .writeLong(msg.sequence())
                .writeInt(bytes.length)
                .writeBytes(bytes);
    }
}
