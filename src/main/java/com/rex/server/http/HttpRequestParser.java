package com.rex.server.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 3/29/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestParser {

    public boolean parse(SocketChannel channel, HttpRequest httpRequest){
        ByteBuffer readBuffer = ByteBuffer.allocate(8192);

        try {
            // Attempt to read off the channel
            int numRead = channel.read(readBuffer);

            if (numRead == -1) {
                // Remote entity shut the socket down cleanly. Do the
                // same from our end and cancel the channel.
                return false;
            }


        } catch (IOException e){
            e.printStackTrace();
            return false;
        }

        return parseBuffer(readBuffer,httpRequest);
    }

    private boolean parseBuffer(ByteBuffer byteBuffer, HttpRequest httpRequest){
        byte[] data = byteBuffer.array();

        String dataAll = new String(data);
        String[] dataSep = dataAll.split("\r\n\r\n");
        String[] dataHeaders = dataSep[0].split("\r\n");
        String body = dataSep[1];

        int linenum = 1;
        for (String line : dataHeaders){
            if (linenum == 1){
                String[] params = line.split(" ");
                httpRequest.setMethod(HttpRequest.Method.valueOf(params[0]));
                httpRequest.setPath(params[1].trim());
                httpRequest.setVersion(params[2].trim());
            }else {
                httpRequest.addHeader(line.split(":")[0].trim(), line.split(":")[1].trim());
            }
            linenum++;
        }

        httpRequest.setBody(body);
        System.out.println(httpRequest.toString());

        return true;
    }


}
