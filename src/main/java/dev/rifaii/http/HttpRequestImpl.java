package dev.rifaii.http;

import dev.rifaii.http.spec.Method;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class HttpRequestImpl implements HttpRequest {

    private final Method method;
    private final String path;
    private final String fullPath;
    private final String protocolVersion;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final byte[] body;

    public HttpRequestImpl(Method method, String path, String fullPath, Map<String, String> headers, Map<String, String> queryParams, byte[] body) {
        this(method, path, fullPath, "HTTP/1.1", headers, queryParams, body);
    }

    public HttpRequestImpl(Method method, String path, String fullPath, Map<String, String> headers, Map<String, String> queryParams) {
        this(method, path, fullPath, "HTTP/1.1", headers, queryParams, null);
    }

    public HttpRequestImpl(
        Method method,
        String path,
        String fullPath,
        String protocolVersion,
        Map<String, String> headers,
        Map<String, String> queryParams,
        byte[] body
    ) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.fullPath = fullPath;
        this.queryParams = queryParams;
        this.body = body;
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

    @Override
    public byte[] getBody() {
        return this.body;
    }
}
