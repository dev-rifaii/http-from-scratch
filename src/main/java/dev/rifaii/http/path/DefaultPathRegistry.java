package dev.rifaii.http.path;

import dev.rifaii.http.HttpRequest;
import dev.rifaii.http.exception.DefinedErrorException;
import dev.rifaii.http.spec.HttpStatusCode;
import dev.rifaii.http.spec.Method;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPathRegistry implements PathRegistry {

    private final Map<String, Map<Method, HttpRequestHandler>> registry = new ConcurrentHashMap<>();

    public void register(HttpPath httpPath) {
        if (!PathMatcher.isValidPath(httpPath.getPath()))
            throw new IllegalArgumentException("Invalid path: " + httpPath.getPath());
        if (registry.get(httpPath.getPath()) != null && registry.get(httpPath.getPath()).get(httpPath.getMethod()) != null)
            throw new IllegalStateException("Path " + httpPath.getPath() + " is already registered.");

        registry.put(httpPath.getPath(), Map.of(httpPath.getMethod(), httpPath.getHttpRequestHandler()));
    }

    public HttpBody dispatch(HttpRequest httpRequest) {
        Optional<Map<Method, HttpRequestHandler>> methodsHandlers = registry.keySet()
            .stream()
            .filter(path -> httpRequest.getPath().matches(getRegexValidPath(path)))
            .findFirst()
            .map(registry::get);

        if (methodsHandlers.isEmpty()) {
            throw new DefinedErrorException(HttpStatusCode.NOT_FOUND);
        }

        return methodsHandlers.map(methodHandler -> methodHandler.get(httpRequest.getMethod()))
            //TODO: replace with a better exception
            .orElseThrow(() -> new DefinedErrorException(HttpStatusCode.METHOD_NOT_ALLOWED))
            .handle(httpRequest);
    }

    private String getRegexValidPath(String path) {
        if (!path.contains("{"))
            return path;
        else
            return path.replaceAll("\\{[^/]+\\}", "[a-zA-Z0-9]+");
    }
}
