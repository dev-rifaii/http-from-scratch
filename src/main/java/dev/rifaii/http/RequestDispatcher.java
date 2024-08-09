package dev.rifaii.http;

import dev.rifaii.http.spec.Method;

public interface RequestDispatcher {

    void dispatch(HttpRequest req, HttpResponse res);

    boolean routeExists(String route, Method method);

}
