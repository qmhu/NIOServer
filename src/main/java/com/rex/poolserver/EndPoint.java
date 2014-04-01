package com.rex.poolserver;

import com.rex.poolserver.http.RequestHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 1/12/14
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class EndPoint {

    private NIOConnector nioConnector;
    private SocketChannel socketChannel;
    private Selector selector;
    private EndPointStatus status;
    private SelectorManager.SelectorSet selectorSet;

    public EndPoint(NIOConnector nioConnector, SocketChannel socketChannel, Selector selector, SelectorManager.SelectorSet selectorSet){
        this.nioConnector = nioConnector;
        this.socketChannel = socketChannel;
        this.selector = selector;
        this.selectorSet = selectorSet;
    }

    public void scheduleRead(){
        SelectionKey key = socketChannel.keyFor(selector);

        // set the key non-readable
        key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
        status = EndPointStatus.READING;
        nioConnector.dispatch(new RequestHandler(nioConnector, socketChannel, this));
    }

    public void schedule(){
        if (status == EndPointStatus.INIT){
            System.out.println("schedule init");
            SelectionKey key = socketChannel.keyFor(selector);

            // set the key readable & writeable
            key.interestOps(key.interestOps() & SelectionKey.OP_READ);
            key.interestOps(key.interestOps() & SelectionKey.OP_WRITE);
        } else if (status == EndPointStatus.CLOSING){
            System.out.println("schedule close");
            SelectionKey key = socketChannel.keyFor(selector);

            try {
                key.channel().close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            key.cancel();
        }
    }

    public EndPointStatus getStatus() {
        return status;
    }

    public void setStatus(EndPointStatus status) {
        this.status = status;
        notifySelectorSet();
    }

    public void notifySelectorSet(){
        selectorSet.addChange(this);
    }
}
