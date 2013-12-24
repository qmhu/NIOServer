package com.rex.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/23/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class NIOServerRequestHandler implements Runnable{

    private SocketChannel channel;

    public NIOServerRequestHandler(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            channel.read(buffer);
            byte[] msg = buffer.array();
            System.out.println("NIOServer receive msg:" + buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
