package com.rex.server.http;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by QQ on 14-4-10.
 */
public class HttpOutput extends ServletOutputStream {

    byte[] bodyBytes;
    HttpResponseWarpper httpResponseWarpper;
    SocketChannel socketChannel;

    public HttpOutput(HttpResponseWarpper httpResponseWarpper,SocketChannel socketChannel){
        this.httpResponseWarpper = httpResponseWarpper;
        this.socketChannel = socketChannel;
    }

    @Override
    public void write(byte[] body) throws IOException {
        this.bodyBytes = new byte[body.length];
        System.arraycopy(body,0,this.bodyBytes,0,body.length);
        httpResponseWarpper.setContentLength(body.length);
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void flush() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        buffer.put(httpResponseWarpper.getVersion().getBytes());
        buffer.put(" ".getBytes());
        buffer.put(String.valueOf(httpResponseWarpper.getStatus()).getBytes());
        buffer.put(HTTPTokens.CRLF);
        for(String headerKey : httpResponseWarpper.getHeaderNames()){
            buffer.put(headerKey.getBytes());
            buffer.put(HTTPTokens.COLON);
            buffer.put(" ".getBytes());
            buffer.put(httpResponseWarpper.getHeader(headerKey).getBytes());
            buffer.put(HTTPTokens.CRLF);
        }

        buffer.put(HTTPTokens.CRLF);
        if (bodyBytes != null){
            buffer.put(bodyBytes);
        }

        System.out.println(new String(buffer.array()));
        buffer.flip();
        socketChannel.write(buffer);
    }

}
