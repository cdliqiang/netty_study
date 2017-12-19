package com.lee.netty.automic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author BazingaLyn 
 * @description 网络传输的唯一对象
 * @time 2016年8月10日
 * @modifytime
 */
public class RemotingTransporter  {

	private static final AtomicLong requestId = new AtomicLong(0l);

	/**
	 * 请求的id
	 */
	private long opaque = requestId.getAndIncrement();


	public long getOpaque() {
		return opaque;
	}

	public void setOpaque(long opaque) {
		this.opaque = opaque;
	}

    public static void main(String[] args) {
        RemotingTransporter t = new RemotingTransporter();
        System.out.println(t.getOpaque());
        System.out.println(t.getOpaque());

        RemotingTransporter t2 = new RemotingTransporter();
        System.out.println(t2.getOpaque());
        System.out.println(t2.getOpaque());


    }

	

}
