package com.rex.server.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 3/29/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpResponseGenerator {

    public HttpServletResponse generalResponse(HttpServletRequest request, SocketChannel socketChannel){
        HttpResponseWarpper httpResponseWapper = new HttpResponseWarpper(socketChannel);
        httpResponseWapper.setStatus(404);
        httpResponseWapper.setContentType("text/html");
        httpResponseWapper.setVersion("HTTP/1.1");

        return httpResponseWapper;
    }
}
