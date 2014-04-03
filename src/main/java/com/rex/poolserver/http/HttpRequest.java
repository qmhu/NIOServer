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

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public enum Method{
        GET("GET"),PUT("PUT"),POST("POST"),DELETE("DELETE"),HEAD("HEAD");

        private String m;

        private Method(String method){
            this.m = method;
        }

    }

    public void addHeader(String key, String value){
        headers.put(key,value);
    }

    public String getHeader(String key){
        return headers.get(key);
    }

    private Method method;

    private String path;

    private String version;

    private Map<String,String> headers = new HashMap<String, String>();

    private String body;




}
