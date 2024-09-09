package dev.rifaiii;

import dev.rifaii.http.HttpServer;
import dev.rifaii.http.path.HttpPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;

public abstract class ITestBase {

    protected final HttpClient httpClient = HttpClient.newHttpClient();

    static HttpServer server;

    @BeforeEach
    void setup() {
        try {
            System.out.println("STARTING TEST SERVER");
            server = new HttpServer(paths());
            server.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static {

    }

    @AfterEach
    void afterAll() throws IOException {
        System.out.println("STOPPING TEST SERVER");
        server.stopListening();
    }

    protected abstract List<HttpPath> paths();
}
