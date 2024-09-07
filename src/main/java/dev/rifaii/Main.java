package dev.rifaii;

import dev.rifaii.http.HttpServer;
import dev.rifaii.http.path.HttpBody;
import dev.rifaii.http.path.HttpPath;
import dev.rifaii.http.spec.Method;

import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(
            List.of(new HttpPath(Method.POST, "/", request -> new HttpBody("Test".getBytes(UTF_8), UTF_8)))
        );
        httpServer.startListening();
    }
}