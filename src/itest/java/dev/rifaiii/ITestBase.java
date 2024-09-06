package dev.rifaiii;

import dev.rifaii.http.HttpServer;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.net.http.HttpClient;

public class ITestBase {

    protected final HttpClient httpClient = HttpClient.newHttpClient();

    static HttpServer server;
    static {
        try {
            System.out.println("STARTING TEST SERVER");
            server = new HttpServer();
            server.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() throws IOException {
        System.out.println("STOPPING TEST SERVER");
        server.stopListening();
    }
}
