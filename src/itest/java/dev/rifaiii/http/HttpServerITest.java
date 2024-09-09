package dev.rifaiii.http;

import dev.rifaii.http.path.HttpBody;
import dev.rifaii.http.path.HttpPath;
import dev.rifaii.http.spec.Method;
import dev.rifaiii.ITestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpServerITest extends ITestBase {

    protected List<HttpPath> paths() {
        return List.of(new HttpPath(Method.POST, "/", request -> new HttpBody("HELLO WORLD".getBytes(UTF_8), UTF_8)));
    }

    @Test
    void sendMultipleRequestsOnSameConnection() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString("LOREM"))
            .uri(new URI("http://127.0.0.1/"))
            .version(Version.HTTP_1_1)
            .build();

        String responseBody1 = httpClient.send(httpRequest, BodyHandlers.ofString()).body();
        String responseBody2 = httpClient.send(httpRequest, BodyHandlers.ofString()).body();

        Assertions.assertEquals("HELLO WORLD", responseBody1);
        Assertions.assertEquals("HELLO WORLD", responseBody2);
    }

}
