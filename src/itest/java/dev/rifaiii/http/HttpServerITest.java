package dev.rifaiii.http;

import dev.rifaiii.ITestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpServerITest extends ITestBase {

    @Test
    void test() throws URISyntaxException, IOException, InterruptedException {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Connection");
        System.out.println("TEST");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("BODYYYYYY"))
                .header("Connection", "close")
                .uri(new URI("http://127.0.0.1/"))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> secondResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
