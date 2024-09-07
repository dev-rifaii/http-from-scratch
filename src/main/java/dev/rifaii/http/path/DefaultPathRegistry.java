package dev.rifaii.http.path;

import dev.rifaii.http.HttpRequest;
import dev.rifaii.http.spec.Method;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPathRegistry implements PathRegistry {

    private final Map<String, Map<Method, HttpRequestHandler>> registry = new ConcurrentHashMap<>();

    public void register(HttpPath path) {
        registry.put(path.getPath(), Map.of(path.getMethod(), path.getHttpRequestHandler()));
    }

    public HttpBody dispatch(HttpRequest httpRequest) {
        Optional<Map<Method, HttpRequestHandler>> methodsHandlers = Optional.ofNullable(registry.get(httpRequest.getPath()));

        return methodsHandlers.map(methodHandler -> methodHandler.get(httpRequest.getMethod()))
            .orElseThrow(RuntimeException::new)
            .handle(httpRequest);
    }
}
