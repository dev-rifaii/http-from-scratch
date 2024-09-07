package dev.rifaiii;

import dev.rifaii.http.HttpServer;
import dev.rifaii.http.path.HttpBody;
import dev.rifaii.http.path.HttpPath;
import dev.rifaii.http.spec.Method;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ITestBase {

    protected final HttpClient httpClient = HttpClient.newHttpClient();

    static HttpServer server;
    static {
        try {
            System.out.println("STARTING TEST SERVER");
            server = new HttpServer(List.of(new HttpPath(Method.POST, "/", request -> new HttpBody("HELLO WORLD".getBytes(UTF_8), UTF_8))));
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
