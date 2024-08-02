package dev.rifaii.http;

import java.util.Map;

//https://datatracker.ietf.org/doc/html/rfc2616#section-3.2.3
public class RequestDispatcherImpl implements RequestDispatcher {

    private final Map<String, HttpHandler> handlers;

    public RequestDispatcherImpl(Map<String, HttpHandler> handlers) {
        this.handlers = handlers;
    }

    public void dispatch(HttpRequest req, HttpResponse res) {
        handlers.get(req.getPath()).handle(req, res);
    }
}
