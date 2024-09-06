package dev.rifaii.http.path;

import dev.rifaii.http.HttpRequest;
import dev.rifaii.http.spec.Method;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PathRegistry {

    private final Map<String, Map<Method, HttpRequestHandler>> registry = new ConcurrentHashMap<>();

    public void register(String path, Method method, HttpRequestHandler handler) {
        registry.put(path, Map.of(method, handler));
    }

    byte[] dispatch(HttpRequest httpRequest) {
        Optional<Map<Method, HttpRequestHandler>> methodsHandlers = Optional.ofNullable(registry.get(httpRequest.getPath()));

        return methodsHandlers.map(methodHandler -> methodHandler.get(httpRequest.getMethod()))
            .orElseThrow(RuntimeException::new)
            .handle(httpRequest);
    }
}
