package com.rex.server.io;

import com.rex.server.Server;
import com.rex.server.ThreadPool;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/26/13
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class NIOConnector extends Thread {

    private SelectorManager selectorManager;
    private AcceptManager acceptManager;
    private ThreadPool threadPool;
    private Server server;

    public NIOConnector(Server server){
        this.server = server;
        this.selectorManager = new SelectorManager(this);
        this.acceptManager = new AcceptManager(this);
        this.threadPool = new ThreadPool();
    }

    public void init(int port) throws IOException {
        acceptManager.init(port);
    }

    public void run(){
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void dispatch(Runnable runnable){
        this.threadPool.dispatch(runnable);
    }


    public void accept(SocketChannel socketChannel) throws IOException {
        selectorManager.registerAccept(socketChannel);
    }

    public void shutdown() {
        System.out.println("begin to shutdown NIOConnector");
        if (this.acceptManager != null){
            this.acceptManager.shutdown();
        }

        if (this.selectorManager != null){
            this.selectorManager.shutdown();
        }

    }

    public Server getServer() {
        return server;
    }
}
