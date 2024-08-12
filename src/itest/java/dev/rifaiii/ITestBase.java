package dev.rifaiii;

import dev.rifaii.http.HttpServer;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;

public class ITestBase {
    static HttpServer server;

    static {
        try {
            server = new HttpServer();
            server.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() throws IOException {
        server.stopListening();
    }
}
