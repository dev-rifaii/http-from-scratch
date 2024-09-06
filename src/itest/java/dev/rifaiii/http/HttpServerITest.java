package dev.rifaiii.http;

import dev.rifaiii.ITestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class HttpServerITest extends ITestBase {

    @Test
    void sendMultipleRequestsOnSameConnection() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString("BODYYYYYY"))
            .uri(new URI("http://127.0.0.1/"))
            .version(Version.HTTP_1_1)
            .build();

        httpClient.send(httpRequest, BodyHandlers.ofString());
        httpClient.send(httpRequest, BodyHandlers.ofString());
    }
}
