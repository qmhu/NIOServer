package com.rex.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/23/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class NIOServerRequestHandler implements Runnable{

    private SelectionKey key;

    public NIOServerRequestHandler(SelectionKey selectionKey) {
        this.key = selectionKey;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(50);
            int numRead;
            try {
                numRead = socketChannel.read(buffer);
            } catch (IOException e) {
                // The remote forcibly closed the connection, cancel
                // the selection key and close the channel.
                e.printStackTrace();
                key.cancel();
                socketChannel.close();
                return;
            }

            if (numRead == -1) {
                // Remote entity shut the socket down cleanly. Do the
                // same from our end and cancel the channel.
                System.out.println("remote socket shutdown");
                key.channel().close();
                key.cancel();
                return;
            }

            byte[] msg = buffer.array();
            System.out.println("NIOServer receive msg:" + msg.toString());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
