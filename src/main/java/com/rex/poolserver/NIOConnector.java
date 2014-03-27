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
    private Selector selector;

    public NIOConnector(Server server){
        this.server = server;
        this.selectorManager = new SelectorManager(this);
        this.acceptManager = new AcceptManager(this);
        this.threadPool = new ThreadPool();
    }

    public void init(int port) throws IOException {
        acceptManager.init(port);

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

    public void run(){
        while (true){
            try {
                synchronized (pendingChanges){
                    for (ChangeEvent changeEvent : pendingChanges){
                        int ops = changeEvent.getOps();
                        switch (ops){
                            case ChangeEvent.READ:
                                //System.out.println("handle ChangeEvent READ");
                                if (changeEvent.getKey().isValid()){
                                    changeEvent.getKey().interestOps(SelectionKey.OP_WRITE);
                                }
                                break;
                            case ChangeEvent.CLOSE:
                                SocketChannel socketChannel = (SocketChannel) changeEvent.getKey().channel();
                                System.out.println("one client close channel" + (socketChannel.socket().getRemoteSocketAddress().toString()));
                                socketChannel.close();
                                changeEvent.getKey().cancel();
                                break;
                            case ChangeEvent.WRITE:
                                System.out.println("handle ChangeEvent WRITE");
                                if (changeEvent.getKey().isValid()){
                                    changeEvent.getKey().interestOps(SelectionKey.OP_READ);
                                }
                                break;
                        }
                    }
                    pendingChanges.clear();
                }

                //this.selector.select();

                Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();

                    if (!key.isValid()){
                        continue;
                    }

                    if (key.isAcceptable()){
                        accept(key);
                    } else if (key.isReadable()){
                        read(key);
                    } else if (key.isWritable()){
                        write(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                try {
                    this.selector.close();
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public void accept(SelectionKey key) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(this.selector,SelectionKey.OP_READ);
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
