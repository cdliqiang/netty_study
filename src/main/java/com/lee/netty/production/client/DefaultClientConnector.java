package com.lee.netty.production.client;

import com.lee.netty.production.ConnectionWatchDog;
import com.lee.netty.production.common.Acknowledge;
import com.lee.netty.production.common.MessageNonAck;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by liqiang on 2017/12/18.
 */
public class DefaultClientConnector extends NettyClientConnector {
    private static final Logger logger = LoggerFactory.getLogger(ConnectIdleTrigger.class);

    private static ConcurrentMap<Long, MessageNonAck> msgMap = new ConcurrentHashMap<Long, MessageNonAck>();
    private Timer timer = new HashedWheelTimer(new DefaultThreadFactory("client timer"));


    private ConnectIdleTrigger idleTrigger = new ConnectIdleTrigger();
    private MessageHandler handler = new MessageHandler();
    private MessageEncoder encoder = new MessageEncoder();
    private AckEncoder ackEncoder = new AckEncoder();

    public DefaultClientConnector() {
        init();
    }

    @Override
    public void init() {
        super.init();
        getBootstrap().channel(NioSocketChannel.class)
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) SECONDS.toMillis(3));

        getBootstrap().option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false);

    }

    @Override
    public Channel connect(final String host, final int port) {
        Bootstrap bts = getBootstrap();
        final ConnectionWatchDog watchDog = new ConnectionWatchDog(bts, host, port, timer) {
            @Override
            public ChannelHandler[] newHandlers() {
                return new ChannelHandler[]{
                        this,
                        new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                        idleTrigger,
                        new ConnectorMessageDecoder(),
                        ackEncoder,
                        encoder,
                        handler
                };
            }
        };
        ChannelFuture future = null;
        Channel channel = null;
        try {
            synchronized (bts) {
                bts.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(watchDog.newHandlers());
                    }
                });
                future = bts.connect(host, port);
            }
            future.sync();
            channel = future.channel();
        } catch (Throwable t) {
            throw new RuntimeException("connects to [" + host + ":" + port + "] fails", t);
        }
        return channel;
    }

    @ChannelHandler.Sharable
    class MessageHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof Acknowledge) {
                Acknowledge acknowledge = (Acknowledge) msg;
                logger.info("client receive ack " + acknowledge.sequence());
                msgMap.remove(acknowledge.sequence());
            }

            ctx.fireChannelRead(msg);
        }
    }


    static {
        Thread t = new Thread(new AckTimeOutScanner(), "ack timeout scanner");
        t.setDaemon(true);
        t.start();
    }

    static class AckTimeOutScanner implements Runnable {

        @Override
        public void run() {
            for (; ; ) {
                for (MessageNonAck m : msgMap.values()) {
                    if ((System.currentTimeMillis() - m.getTimestamp()) - 300 > 0) {
                        if (msgMap.remove(m.getId()) == null) {
                            continue;
                        }
                        Channel channel = m.getChannel();
                        if (channel.isActive()) {
                            MessageNonAck msgNonAck = new MessageNonAck(m.getMsg(), m.getChannel());
                            msgMap.put(msgNonAck.getId(), msgNonAck);
                            channel.writeAndFlush(msgNonAck.getMsg()).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                        }
                    }
                }
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void addEventMsg(MessageNonAck msg) {
        msgMap.put(msg.getId(), msg);
    }
}

