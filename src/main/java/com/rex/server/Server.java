package com.rex.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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

                if (selectionKey.isAcceptable()){
                    ServerSocketChannel socketChannel = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel channel = socketChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(this.selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()){
                    System.out.println("one socket is readable");
                    SocketChannel channel = (SocketChannel)selectionKey.channel();
                    threadPoolExecutor.submit(new NIOServerRequestHandler(channel));
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
