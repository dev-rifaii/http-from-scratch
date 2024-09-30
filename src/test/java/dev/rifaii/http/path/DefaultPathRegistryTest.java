package dev.rifaii.http.path;

import dev.rifaii.http.HttpRequestImpl;
import dev.rifaii.http.spec.Method;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.*;

class DefaultPathRegistryTest {

    DefaultPathRegistry registry = new DefaultPathRegistry();

    @Test
    void dispatch() {
        registry.register(new HttpPath(
            Method.GET,
            "/payments/{paymentId}/{test}",
            httpRequest -> new HttpBody("test".getBytes(), StandardCharsets.UTF_8)
        ));


        registry.dispatch(
            new HttpRequestImpl(Method.GET, "/payments/41/51", "/payments/41/51", emptyMap(), emptyMap())
        );
    }
}