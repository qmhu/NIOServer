package com.rex.poolserver;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 1/11/14
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Server {

    private NIOConnector connector;
    private ServerHandler serverHandler;

    public void init(int port) throws IOException {
        connector = new NIOConnector(this);
        connector.init(port);
    }

    public void setConnector(ServerHandler serverHandler){
        this.serverHandler = serverHandler;
    }

    public ServerHandler getServerHandler(){
        return this.serverHandler;
    }

    public void start(){
        while (true){
            connector.start();

            try {
                connector.join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init(80);
        server.start();
    }

}
