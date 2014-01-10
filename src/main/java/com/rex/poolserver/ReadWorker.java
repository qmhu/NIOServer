package com.rex.poolserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/26/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadWorker extends Thread{

    private SelectionKey key;
    private ThreadPool threadPool;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
    private PoolNIOServer server;

    public ReadWorker(ThreadPool threadPool, PoolNIOServer server){
        this.threadPool = threadPool;
        this.server = server;
    }

    public synchronized void handleKey(SelectionKey key){
        this.key = key;
        this.notify();
    }

    public synchronized void run(){
        while (true){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.out.println(getName() + " is weakup");

            if (key == null){
                System.out.println("key is empty");
                continue;
            }

            try {
                process(key);
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                key = null;
            }

            threadPool.returnWorker(this);
        }
    }

    public void process(SelectionKey key) throws IOException{
        byteBuffer.clear();
        SocketChannel socketChannel = (SocketChannel) key.channel();

        int count;
        try{
            count = socketChannel.read(byteBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            count = -1;
        }

        System.out.println("count:" + count);
        if (count < 0){
            // The remote shutdown gracefully
            server.handleChangeEvent(new ChangeEvent(key,ChangeEvent.CLOSE,null));
        }

        server.handleChangeEvent(new ChangeEvent(key,ChangeEvent.READ,byteBuffer.array()));
    }
}