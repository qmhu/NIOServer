package com.rex.server.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 1/12/14
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class AcceptManager {

    private NIOConnector nioConnector;
    private ServerSocketChannel serverSocketChannel;
    private Thread listenThread;

    public AcceptManager(NIOConnector nioConnector){
        this.nioConnector = nioConnector;
    }

    public void init(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        serverSocketChannel.configureBlocking(true);
        beginListen();
    }

    public void beginListen(){
        Runnable listenHandler = new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("Newer client has connected:" + socketChannel.socket().getRemoteSocketAddress());

                        nioConnector.accept(socketChannel);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }


                }
            }
        };

        listenThread = new Thread(listenHandler);
        listenThread.start();
    }

    public void shutdown() {
        if (this.serverSocketChannel != null){
            try {
                this.serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }
}
