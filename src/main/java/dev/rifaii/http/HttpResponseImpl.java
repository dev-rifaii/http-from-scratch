package dev.rifaii.http;

import dev.rifaii.http.spec.HttpStatusCode;

import java.io.OutputStream;

public final class HttpResponseImpl implements HttpResponse {

    private HttpStatusCode sc;
    private final OutputStream os;

    public HttpResponseImpl(OutputStream os) {
        this(os, null);
    }

    public HttpResponseImpl(OutputStream os, HttpStatusCode sc) {
        this.os = os;
        this.sc = sc;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return this.sc;
    }

    @Override
    public void setStatusCode(HttpStatusCode status) {
        this.sc = status;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.os;
    }
}
