package dev.rifaii.http;

import java.util.Map;

public class RequestDispatcher {

    private final Map<String, HttpHandler> handlers;

    public RequestDispatcher(Map<String, HttpHandler> handlers) {
        this.handlers = handlers;
    }

    public void dispatch(HttpRequest req, HttpResponse res) {
        handlers.get(req.getPath()).handle(req, res);
    }
}
