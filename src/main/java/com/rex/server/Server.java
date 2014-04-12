package com.rex.server;

import com.rex.server.io.NIOConnector;
import com.rex.server.servlet.impl.MyServletContext;
import com.rex.server.servlet.ServletManager;

import javax.servlet.Servlet;
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
    private ServletManager servletManager;

    public Server(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                shutdown();
            }
        });
        servletManager = new ServletManager();
    }

    private void shutdown() {
        if (this.connector != null){
            this.connector.shutdown();
        }
    }

    public void init(int port) throws IOException {
        connector = new NIOConnector(this);
        connector.init(port);
    }

    public void registServlet(String path,Servlet servlet){
        this.servletManager.regist(path, servlet);
    }

    public Servlet getServlet(String path){
        return this.servletManager.getServlet(path);
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
        server.registServlet("/",new MyServletContext());
        server.init(8080);
        server.start();
    }

}
