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
            ByteBuffer buffer = ByteBuffer.allocate(100);
            int count;
            buffer.clear(); // Empty buffer
            // Loop while data is available; channel is nonblocking
            while ((count = socketChannel.read(buffer)) > 0) {
                System.out.println("sub count:" + count);
                buffer.flip(); // make buffer readable
                // Send the data; may not go all at once

                System.out.println("NIOServer receive msg:" + new String(buffer.array()));


                // WARNING: the above loop is evil.
                // See comments in superclass.
                buffer.clear(); // Empty buffer
            }


            if (count < 0) {
                // Close channel on EOF; invalidates the key
                System.out.println("one client close channel" + ((SocketChannel) key.channel()).socket().getRemoteSocketAddress().toString());
                socketChannel.close();
                key.cancel();
                return;
            }

            // Resume interest in OP_READ
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            // Cycle the selector so this key is active again
            //key.selector().wakeup();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
