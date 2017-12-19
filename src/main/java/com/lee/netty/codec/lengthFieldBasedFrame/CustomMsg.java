package com.lee.netty.codec.lengthFieldBasedFrame;

/**
 * Created by liqiang on 2017/10/24.
 */
public class CustomMsg {
    private byte type;
    private byte flag;
    private String body;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
