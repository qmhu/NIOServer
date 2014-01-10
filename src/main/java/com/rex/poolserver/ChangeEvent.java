package com.rex.poolserver;

import java.nio.channels.SelectionKey;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/26/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeEvent {

    public static final int READ = 0;
    public static final int WRITE = 1;
    public static final int CLOSE = 2;

    private SelectionKey key;
    private int ops;
    private byte[] data;

    public ChangeEvent(SelectionKey key, int ops, byte[] data){
        this.key = key;
        this.ops = ops;
        this.data = data;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public int getOps() {
        return ops;
    }

    public void setOps(int ops) {
        this.ops = ops;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
