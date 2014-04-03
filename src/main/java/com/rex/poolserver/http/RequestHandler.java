package com.rex.poolserver.http;

import com.rex.poolserver.EndPoint;
import com.rex.poolserver.EndPointStatus;
import com.rex.poolserver.NIOConnector;

import java.nio.channels.SocketChannel;

/**
 * Created by QQ on 14-3-27.
 */
public class RequestHandler implements Runnable {

    NIOConnector nioConnector;
    SocketChannel socketChannel;
    EndPoint point;
    HttpRequestParser requestParser = new HttpRequestParser();
    HttpResponseGenerator responseGenerator = new HttpResponseGenerator();

    public RequestHandler(NIOConnector nioConnector, SocketChannel socketChannel, EndPoint point){
        this.nioConnector = nioConnector;
        this.socketChannel = socketChannel;
        this.point = point;
    }

    @Override
    public void run() {
        System.out.println("get a new HTTP Request");
        HttpRequest httpRequest = new HttpRequest();

        if (!requestParser.parse(socketChannel,httpRequest)){
            // remote close the channel or something error happens durning the reading
            point.setStatus(EndPointStatus.CLOSING);
            return;
        }

        point.setStatus(EndPointStatus.INIT);
    }
}
