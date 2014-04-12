package com.rex.server.http;

import com.rex.server.io.EndPoint;
import com.rex.server.io.EndPointStatus;
import com.rex.server.io.NIOConnector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        HttpServletRequest httpRequestWarpper = new HttpRequestWarpper(httpRequest);
        HttpServletResponse httpResponseWapper = responseGenerator.generalResponse(httpRequestWarpper,socketChannel);
        try {
            nioConnector.getServer().getServlet(httpRequestWarpper.getServletPath()).service(httpRequestWarpper,httpResponseWapper);
            httpResponseWapper.getOutputStream().flush();
        } catch (ServletException e) {
            e.printStackTrace();
            httpResponseWapper.setStatus(500);
        } catch (IOException e) {
            e.printStackTrace();
            httpResponseWapper.setStatus(500);
        }finally {
            point.setStatus(EndPointStatus.INIT);
        }
    }
}
