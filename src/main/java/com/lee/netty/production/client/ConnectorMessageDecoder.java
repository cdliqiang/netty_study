package com.lee.netty.production.client;

import com.lee.netty.production.common.Acknowledge;
import com.lee.netty.production.common.Message;
import com.lee.netty.production.common.NettyCommonProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.Signal;

import java.util.List;

import static com.lee.netty.production.common.NettyCommonProtocol.*;
import static com.lee.netty.production.serializer.SerializerHolder.serializerImpl;

/**
 * Created by liqiang on 2017/12/15.
 */
public class ConnectorMessageDecoder extends ReplayingDecoder<ConnectorMessageDecoder.State> {

    NettyCommonProtocol protocol = new NettyCommonProtocol();

    public ConnectorMessageDecoder() {
        super(State.HEADER_MAGIC);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case HEADER_MAGIC:
                checkMagic(in.readShort());
                checkpoint(State.HEADER_SIGN);
            case HEADER_SIGN:
                protocol.sign(in.readByte());
                checkpoint(State.HEADER_STATUS);
            case HEADER_STATUS:
                protocol.status(in.readByte());
                checkpoint(State.HEADER_ID);
            case HEADER_ID:
                protocol.id(in.readLong());
                checkpoint(State.HEADER_BODY_LENGTH);
            case HEADER_BODY_LENGTH:
                protocol.bodyLength(in.readInt());
                checkpoint(State.BODY);
            case BODY:
                switch (protocol.sign()) {
                    case RESPONSE:
                    case SERVICE_1:
                    case SERVICE_2:
                    case SERVICE_3:
                    case SERVICE_4: {
                        byte[] bytes = new byte[protocol.bodyLength()];
                        in.readBytes(bytes);

                        Message msg = serializerImpl().readObject(bytes, Message.class);
                        msg.sign(protocol.sign());
                        out.add(msg);

                        break;
                    }
                    case ACK: {
                        byte[] bytes = new byte[protocol.bodyLength()];
                        in.readBytes(bytes);

                        Acknowledge msg = serializerImpl().readObject(bytes, Acknowledge.class);
                        out.add(msg);

                        break;
                    }
                    default:
                        throw new IllegalAccessException();
                }
                checkpoint(State.HEADER_MAGIC);
        }
    }

    private void checkMagic(Short magic) throws Signal {
        if (magic != protocol.MAGIC) {
            throw new IllegalArgumentException("error magic");
        }
    }

    enum State {
        HEADER_MAGIC,
        HEADER_SIGN,
        HEADER_STATUS,
        HEADER_ID,
        HEADER_BODY_LENGTH,
        BODY
    }

}
