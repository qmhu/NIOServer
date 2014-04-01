package com.rex.poolserver.http;

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
            System.out.println("parse begin");

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
        System.out.println("buffer :" + new String(data));

        int i = 0;
        while (i < data.length){
            if (i == HTTPTokens.CRLF && i == 0)

        }

        return true;
    }


}
