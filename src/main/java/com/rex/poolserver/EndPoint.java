package com.rex.poolserver;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 1/12/14
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class EndPoint {

    private NIOConnector nioConnector;
    private SocketChannel socketChannel;
    private Selector selector;

    public EndPoint(NIOConnector nioConnector, SocketChannel socketChannel, Selector selector){
        this.nioConnector = nioConnector;
        this.socketChannel = socketChannel;
        this.selector = selector;
    }



}
