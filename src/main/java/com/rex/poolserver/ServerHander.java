package com.rex.poolserver;

/**
 * Created by QQ on 14-1-15.
 */
public interface ServerHander {

    public void OnAccept();

    public void OnRead();

    public void OnWrite();

    public void OnClose();
}
