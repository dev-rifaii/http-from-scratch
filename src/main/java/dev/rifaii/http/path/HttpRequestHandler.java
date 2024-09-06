package dev.rifaii.http.path;

import dev.rifaii.http.HttpRequest;

public interface HttpRequestHandler {

    byte[] handle(HttpRequest request);
}
