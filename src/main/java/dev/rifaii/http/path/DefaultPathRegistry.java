package dev.rifaii.http.path;

import dev.rifaii.http.HttpRequest;
import dev.rifaii.http.exception.PathNotFoundException;
import dev.rifaii.http.spec.Method;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPathRegistry implements PathRegistry {

    private final Map<String, Map<Method, HttpRequestHandler>> registry = new ConcurrentHashMap<>();

    public void register(HttpPath path) {
        if (!PathMatcher.isValidPath(path.getPath())) {
            throw new IllegalArgumentException("Invalid path: " + path.getPath());
        }
        registry.put(path.getPath(), Map.of(path.getMethod(), path.getHttpRequestHandler()));
    }

    public HttpBody dispatch(HttpRequest httpRequest) {
        Optional<Map<Method, HttpRequestHandler>> methodsHandlers = registry.keySet()
            .stream()
            .filter(path -> getRegexValidPath(path).matches(httpRequest.getPath()))
            .findFirst()
            .map(registry::get);

        if (methodsHandlers.isEmpty()) {
            throw new PathNotFoundException("Path %s not found".formatted(httpRequest.getPath()));
        }

        return methodsHandlers.map(methodHandler -> methodHandler.get(httpRequest.getMethod()))
            .orElseThrow(RuntimeException::new)
            .handle(httpRequest);
    }

    private String getRegexValidPath(String path) {
        if (!path.contains("{"))
            return path;
        else
            return path.replaceAll("\\{[^/]+\\}", "[a-zA-Z0-9]+");
    }
}
