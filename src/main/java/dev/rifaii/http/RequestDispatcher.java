package dev.rifaii.http;

public interface RequestDispatcher {

    void dispatch(HttpRequest req, HttpResponse res);

}
