package com.lee.netty.codec.lengthFieldBasedFrame.server;

import com.lee.netty.codec.lengthFieldBasedFrame.CustomMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by liqiang on 2017/10/24.
 */
public class CustomMsgDecoder extends LengthFieldBasedFrameDecoder {

    public CustomMsgDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    /**
     * TODO 当消息帧length超过1024，LengthFieldBasedFrameDecoder会进行多次decode，会报异常
     *
     * @param ctx
     * @param in
     * @return
     * @throws Exception
     */
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Byte flag = in.readByte();
        Byte type = in.readByte();
        int length = in.readInt();
        byte[] bodyByte = new byte[length];
        in.readBytes(bodyByte);
        String body = new String(bodyByte);
        CustomMsg msg = new CustomMsg();
        msg.setBody(body);
        msg.setFlag(flag);
        msg.setType(type);
        return msg;
    }
}
