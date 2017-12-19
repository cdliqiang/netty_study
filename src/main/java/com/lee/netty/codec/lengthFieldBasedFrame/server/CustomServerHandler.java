package com.lee.netty.codec.lengthFieldBasedFrame.server;

import com.lee.netty.codec.lengthFieldBasedFrame.CustomMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by liqiang on 2017/10/24.
 */
public class CustomServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] array = new byte[buf.readableBytes()];
//        buf.readBytes(array);
//        String body = new String(array);
//        System.out.println(body);


//        byte flag = buf.readByte();
//        byte type = buf.readByte();
//        int length = buf.readInt();
//        byte[] array = new byte[length];
//        buf.readBytes(array);
//        String body = new String(array);
//        System.out.println(body);

//        if (msg != null && msg instanceof CustomMsg) {
//            CustomMsg message = (CustomMsg) msg;
//            System.out.println("receive customMsg " + message.getFlag() + "; " + message.getType() + "; " + message.getBody());
//        }
        ByteBuf buf = (ByteBuf) msg;
        byte[] body = new byte[buf.readableBytes()];
        buf.readBytes(body);
        System.out.println(new String(body));
    }
}
