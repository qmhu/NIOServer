package com.rex.poolserver;

import javax.jws.soap.SOAPBinding;
import java.nio.channels.SocketChannel;

/**
 * Created by QQ on 14-3-27.
 */
public class RequestHandler implements Runnable {

    NIOConnector nioConnector;
    SocketChannel socketChannel;
    EndPoint point;

    RequestHandler(NIOConnector nioConnector, SocketChannel socketChannel, EndPoint point){
        this.nioConnector = nioConnector;
        this.socketChannel = socketChannel;
        this.point = point;
    }

    @Override
    public void run() {

    }
}
