package dev.rifaii.http;

import java.io.OutputStream;

public class HttpResponseBuilderImpl implements HttpResponseBuilder {
    @Override
    public HttpResponse build(OutputStream out) {
        return new HttpResponseImpl(out);
    }
}
