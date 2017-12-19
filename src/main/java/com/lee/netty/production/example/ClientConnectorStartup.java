package com.lee.netty.production.example;

import com.lee.netty.production.client.DefaultClientConnector;
import com.lee.netty.production.common.Message;
import com.lee.netty.production.common.MessageNonAck;
import com.lee.netty.production.common.NettyCommonProtocol;
import com.lyncc.netty.production.srv.acceptor.DefaultCommonSrvAcceptor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liqiang on 2017/12/18.
 */
public class ClientConnectorStartup {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCommonSrvAcceptor.class);

    public static void main(String[] agrs) {
        DefaultClientConnector client = new DefaultClientConnector();
        Channel channel = client.connect("127.0.0.1", 9000);
        Message msg = new Message();
        msg.sign(NettyCommonProtocol.REQUEST);
        msg.data(new UserDomain("lee", 27));
        ChannelFuture future = channel.writeAndFlush(msg);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    logger.error("client send request error");
                }
            }
        });
        MessageNonAck msgNonAck = new MessageNonAck(msg, channel);
        client.addEventMsg(msgNonAck);

    }

    static class UserDomain {
        private String name;
        private int age;

        UserDomain(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
