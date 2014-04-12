package com.rex.server.servlet.impl;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by QQ on 14-4-8.
 */
public class MyServletContext extends HttpServlet{
    private String message;

    public void init() throws ServletException {
        // Do required initialization
        message = "Hello World";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        // Actual logic goes here.
        ServletOutputStream out = response.getOutputStream();
        String msg = "<h1>hello world</h1>";
        out.write(msg.getBytes());
        response.setStatus(200);
    }

    public void destroy() {
        // do nothing.
    }
}
