package com.rex.poolserver;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 1/12/14
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectorManager {

    private NIOConnector nioConnector;
    private SelectorSet[] selectorSets;
    private int selectorIndex = 0;

    public SelectorManager(NIOConnector nioConnector){
        this(nioConnector, 0);
    }

    public SelectorManager(NIOConnector nioConnector,int selectorNum){
        this.nioConnector = nioConnector;
        selectorNum = (selectorNum == 0) ? Runtime.getRuntime().availableProcessors() + 1 : 4;
        for (int i=0; i < selectorNum; i++){
            SelectorSet set = null;
            try {
                set = new SelectorSet(i);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            selectorSets[i] = set;
        }
    }

    public void doSelect(){
        for (SelectorSet selectorSet : selectorSets) {
            selectorSet.start();
        }
    }

    private int getSelectorIndex(){
        int indexNow = selectorIndex;

        if (selectorIndex + 1 > selectorSets.length){
            selectorIndex = 0;
        }else {
            selectorIndex++;
        }

        return indexNow;
    }

    public void registerAccept(SocketChannel socketChannel) throws IOException {
        socketChannel.configureBlocking(false);
        SelectorSet selectorSet = selectorSets[getSelectorIndex()];
        selectorSet.registerAccept(socketChannel);
    }

    public EndPoint newEndPoint(SocketChannel socketChannel,Selector selector){
        return new EndPoint(this.nioConnector,socketChannel,selector);
    }

    class SelectorSet extends Thread {
        int id;
        Selector selector;
        CopyOnWriteArrayList<Object> changes;

        SelectorSet(int id) throws IOException {
            this.id = id;
            this.selector = Selector.open();
            this.changes = new CopyOnWriteArrayList<Object>();
        }

        public void registerAccept(SocketChannel socketChannel){
            this.changes.add(socketChannel);
        }

        public void run(){
            while (true){
                for (Object change : changes) {
                    try{
                        if (change instanceof SocketChannel){
                            // new client connected
                            SocketChannel socketChannel = (SocketChannel)change;
                            SelectionKey selectionKey = socketChannel.register(this.selector, SelectionKey.OP_READ);
                            EndPoint endPoint = newEndPoint(socketChannel, this.selector);
                            selectionKey.attach(endPoint);
                        }
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        System.out.println("handle changes meet error");
                    }

                }

                changes.clear();

                try {
                    this.selector.selectNow();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


            }



        }


    }
}
