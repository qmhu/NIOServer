package com.rex.poolserver.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 3/29/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequest {

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public enum Method{
        GET,PUT,POST,DELETE,HEAD
    }

    private Method method;

    private Map<String,String> headers = new HashMap<String, String>();

    private String body;




}
