package dev.rifaii.http;

import dev.rifaii.http.spec.Method;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

public class HttpRequestImpl implements HttpRequest {

    private Method method;
    private String path;
    private String fullPath;
    private String protocolVersion;
    private Map<String, String> headers;
    private Map<String, String> queryParams;


    public HttpRequestImpl(Method method, String path, String fullPath, Map<String, String> headers, Map<String, String> queryParams) {
        this(method, path, fullPath, "HTTP/1.1", headers, queryParams);
    }

    public HttpRequestImpl(Method method, String path, String fullPath, String protocolVersion, Map<String, String> headers, Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.fullPath = fullPath;
        this.queryParams = queryParams;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String getFullPath() {
        return this.fullPath;
    }

    @Override
    public Map<String, String> getQueryParams() {
        return this.queryParams;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String getHeader(String name) {
        return this.headers.get(name);
    }
}
