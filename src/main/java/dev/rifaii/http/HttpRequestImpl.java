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
    private Optional<String> body;

    public HttpRequestImpl(Method method, String path, Map<String, String> headers, String body) {
        this(method, path, "HTTP/1.1", headers, body);
    }

    public HttpRequestImpl(Method method, String path, String protocolVersion, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.body = null == body ? Optional.empty() : Optional.of(body);
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

    public Optional<String> getBody() {
        return body;
    }
}
