package dev.rifaii.http;

import dev.rifaii.http.spec.Method;
import dev.rifaii.util.Pair;

import java.util.Map;

//https://datatracker.ietf.org/doc/html/rfc2616#section-3.2.3
public class RequestDispatcherImpl implements RequestDispatcher {

    private final Map<Pair<String, Method>, HttpHandler> handlers;

    public RequestDispatcherImpl(Map<Pair<String, Method>, HttpHandler> handlers) {
        this.handlers = handlers;
    }

    public void dispatch(HttpRequest req, HttpResponse res) {
        handlers.get(Pair.of(req.getPath(), req.getMethod())).handle(req, res);
    }

    @Override
    public boolean routeExists(String route, Method method) {
        return handlers.get(Pair.of(route, method)) != null;
    }
}
