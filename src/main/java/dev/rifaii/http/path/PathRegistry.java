package dev.rifaii.http.path;

import dev.rifaii.http.HttpRequest;

public interface PathRegistry {

    void register(HttpPath httpPath);

    HttpBody dispatch(HttpRequest httpRequest);
}
