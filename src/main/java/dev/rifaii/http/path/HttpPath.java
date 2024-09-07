package dev.rifaii.http.path;

import dev.rifaii.http.spec.Method;

public class HttpPath {

    private Method method;
    private String path;
    private HttpRequestHandler httpRequestHandler;

    public HttpPath(Method method, String path, HttpRequestHandler httpRequestHandler) {
        this.method = method;
        this.path = path;
        this.httpRequestHandler = httpRequestHandler;
    }

    public Method getMethod() {
        return method;
    }

    public HttpPath setMethod(Method method) {
        this.method = method;
        return this;
    }

    public String getPath() {
        return path;
    }

    public HttpPath setPath(String path) {
        this.path = path;
        return this;
    }

    public HttpRequestHandler getHttpRequestHandler() {
        return httpRequestHandler;
    }

    public HttpPath setHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
        return this;
    }
}
