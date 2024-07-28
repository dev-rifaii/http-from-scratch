package dev.rifaii.http;

import dev.rifaii.http.spec.Method;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

public class HttpRequestImpl implements HttpRequest {

    private Method method;
    private String path;
    private String protocolVersion;
    private Map<String, String> headers;

    public HttpRequestImpl(Method method, String path, Map<String, String> headers) {
        this(method, path, "HTTP/1.1", headers);
    }

    public HttpRequestImpl(Method method, String path, String protocolVersion, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
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

    public String getProtocolVersion() {
        return protocolVersion;
    }

}
