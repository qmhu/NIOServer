package com.rex.server.servlet;

import javax.servlet.Servlet;
import java.util.HashMap;

/**
 * Created by QQ on 14-4-8.
 */
public class ServletManager {

    private HashMap<String,Servlet> servletMap = new HashMap<String, Servlet>();

    public void regist(String path, Servlet servlet) {
        servletMap.put(path,servlet);
    }

    public Servlet getServlet(String path){
        return servletMap.get(path);
    }
}
