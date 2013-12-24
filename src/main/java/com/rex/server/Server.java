package com.rex.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/23/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class Server{

    private Selector selector;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public void init(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void listen() throws IOException {
        while (true){
            this.selector.select();

            Iterator it = this.selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey selectionKey = (SelectionKey)it.next();
                it.remove();
                if (!selectionKey.isValid()){
                    continue;
                }

                if (selectionKey.isAcceptable()){
                    ServerSocketChannel socketChannel = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel channel = socketChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(this.selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()){
                    try {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(2);
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

                        System.out.println("count:" + count);

                        if (count < 0) {
                            // Close channel on EOF; invalidates the key
                            socketChannel.close();
                            selectionKey.cancel();
                            return;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }


                    //threadPoolExecutor.submit(new NIOServerRequestHandler(selectionKey));
                }

            }

        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init(7778);
        server.listen();
    }


}
