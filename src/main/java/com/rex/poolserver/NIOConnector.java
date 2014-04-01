package com.rex.poolserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    private ServerSocketChannel serverSocketChannel;
    private ReadEventHandler readEventHandler;
    private WriteEventHandler writeEventHandler;
    private List<ChangeEvent> pendingChanges;
    private Map<SocketChannel,Queue<byte[]>> pendingData;
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

    public void handleChangeEvent(ChangeEvent changeEvent){
        synchronized (pendingChanges){
            pendingChanges.add(changeEvent);

            synchronized (pendingData){
                SocketChannel socketChannel = (SocketChannel) changeEvent.getKey().channel();
                Queue<byte[]> datalist = pendingData.get(socketChannel);
                if (datalist == null){
                    datalist = new LinkedList<byte[]>();
                    datalist.add(changeEvent.getData());
                    pendingData.put(socketChannel, datalist);
                }else {
                    datalist.add(changeEvent.getData());
                }
            }
        }

        //selector.wakeup();
    }

    public void dispatch(Runnable runnable){
        this.threadPool.dispatch(runnable);
    }

    public void accept(SocketChannel socketChannel) throws IOException {
        selectorManager.registerAccept(socketChannel);
    }

    public void read(SelectionKey key){
        key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
        readEventHandler.handleReadEvent(key);
    }

    public void write(SelectionKey key){
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (pendingData){
            Queue<byte[]> datalist = pendingData.get(socketChannel);

            while (!datalist.isEmpty()){
                byte[] data = datalist.remove();
                writeEventHandler.handleWriteEvent(key,data);
                /*try {
                    System.out.println("Writing data[" + new String(data) + "]to Client:" + socketChannel.socket().getRemoteSocketAddress().toString());
                    socketChannel.write(ByteBuffer.wrap(data));
                } catch (IOException e){
                    e.printStackTrace();
                }*/
            }
        }

        // set key to OP_READ when all data have bean sended
        key.interestOps(SelectionKey.OP_READ);
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

    class ReadEventHandler{

        ThreadPool threadPool;

        ReadEventHandler(int handlerNum, NIOConnector server){
            //threadPool = new ThreadPool(handlerNum, server);
        }

        public void handleReadEvent(SelectionKey key){
            //ReadWorker readWorker = threadPool.getWorker();
            //readWorker.handleKey(key);
        }
    }

    class WriteEventHandler{

        private ThreadPoolExecutor threadPoolExecutor;
        private NIOConnector server;

        WriteEventHandler(int handlerNum, NIOConnector server){
            threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(handlerNum);
        }

        public void handleWriteEvent(final SelectionKey key, final byte[] data){
            threadPoolExecutor.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        System.out.println("Writing data[" + new String(data) + "]to Client:" + socketChannel.socket().getRemoteSocketAddress().toString());
                        socketChannel.write(ByteBuffer.wrap(data));
                        server.handleChangeEvent(new ChangeEvent(key,ChangeEvent.WRITE,data));
                    } catch (IOException e){
                        e.printStackTrace();
                        server.handleChangeEvent(new ChangeEvent(key,ChangeEvent.CLOSE, null));
                    }

                    return null;
                }
            });
        }

    }
}
