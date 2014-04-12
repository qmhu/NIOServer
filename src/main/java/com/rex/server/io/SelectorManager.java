package com.rex.server.io;

import com.rex.server.io.EndPoint;
import com.rex.server.io.NIOConnector;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        selectorSets = new SelectorSet[selectorNum];
        for (int i=0; i < selectorNum; i++){
            SelectorSet set = null;
            try {
                set = new SelectorSet(i);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            selectorSets[i] = set;
        }
        doSelect();
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
        selectorSet.wakeUp();
    }

    public EndPoint newEndPoint(SocketChannel socketChannel,Selector selector, SelectorSet selectorSet){
        return new EndPoint(this.nioConnector,socketChannel,selector, selectorSet);
    }

    public void shutdown() {


    }

    class SelectorSet extends Thread {
        int id;
        Selector selector;
        ConcurrentLinkedQueue<Object> changes;

        SelectorSet(int id) throws IOException {
            this.id = id;
            this.selector = Selector.open();
            this.changes = new ConcurrentLinkedQueue<Object>();
        }

        public void registerAccept(SocketChannel socketChannel){
            System.out.println("selector set " + id + " has accept .");
            this.changes.add(socketChannel);
        }

        public void wakeUp(){
            selector.wakeup();
        }

        public void addChange(Object o){
            this.changes.add(o);
        }

        public void run(){
            while (true){
                int size = changes.size();
                Object change;

                while (size-- > 0 && (change = changes.poll()) != null){
                    System.out.println("get a change");
                    try{
                        if (change instanceof SocketChannel){
                            // new client connected
                            SocketChannel socketChannel = (SocketChannel)change;
                            SelectionKey selectionKey = socketChannel.register(this.selector, SelectionKey.OP_READ);
                            EndPoint endPoint = newEndPoint(socketChannel, this.selector, this);
                            selectionKey.attach(endPoint);
                            //endPoint.scheduleRead();
                        } else if(change instanceof EndPoint){
                            // handle EndPoint status transfor
                            EndPoint point = (EndPoint)change;

                            point.schedule();
                        }
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        System.out.println("handle changes meet error");
                    }
                }

                int selectNum = 0;
                try {
                    selectNum = this.selector.selectNow();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                if (selectNum == 0){
                    // sleep while not select any events
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();

                while (it.hasNext()){
                    try{
                        SelectionKey key = (SelectionKey)it.next();
                        it.remove();

                        if (!key.isValid()){
                            continue;
                        }

                        if (key.isReadable()){
                            EndPoint point = (EndPoint)key.attachment();
                            point.scheduleRead();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                this.selector.selectedKeys().clear();

            }



        }


    }
}
