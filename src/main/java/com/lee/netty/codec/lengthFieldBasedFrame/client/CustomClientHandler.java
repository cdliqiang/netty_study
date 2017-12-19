package com.lee.netty.codec.lengthFieldBasedFrame.client;

import com.lee.netty.codec.lengthFieldBasedFrame.CustomMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by liqiang on 2017/10/24.
 */
public class CustomClientHandler extends ChannelInboundHandlerAdapter {

    String body = "In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. His book w"
            + "ill give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the process "
            + "of configuring and connecting all of Netty’s components to bring your learned about threading models in ge"
            + "neral and Netty’s threading model in particular, whose performance and consistency advantages we discuss"
            + "ed in detail In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. Hi"
            + "s book will give We’ve reached an exciting point—in the next chapter we’ll discuss bootstrapping, the"
            + " process of configuring and connecting all of Netty’s components to bring your learned about threading "
            + "models in general and Netty’s threading model in particular, whose performance and consistency advantag"
            + "es we discussed in detailIn this chapter you general, we recommend Java Concurrency in Practice by Bri"
            + "an Goetz. His book will give We’ve reached an exciting point—in the next chapter;the counter is: 1 2222"
            + "sdsa ddasd asdsadas dsadasdas";


    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CustomMsg msg = new CustomMsg();
        msg.setType(new Byte("1"));
        msg.setFlag(new Byte("2"));
        msg.setBody(body);
        ctx.writeAndFlush(msg);
        CustomMsg msg2 = new CustomMsg();
        msg2.setType(new Byte("3"));
        msg2.setFlag(new Byte("4"));
        msg2.setBody(body);
        ctx.writeAndFlush(msg2);
    }
}
