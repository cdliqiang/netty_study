package com.lee.netty.production.example;

import com.lee.netty.production.server.DefaultCommonSrvAcceptor;
import com.lee.netty.production.server.DefaultSrvAcceptor;

/**
 * Created by liqiang on 2017/12/15.
 */
public class SrvAcceptorStartup {

    public static void main(String[] args) throws Exception {
        DefaultCommonSrvAcceptor server = new DefaultCommonSrvAcceptor(9000, null);
        server.start();
    }
}
