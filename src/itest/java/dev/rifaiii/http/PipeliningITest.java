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
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PipeliningITest extends ITestBase {

    protected List<HttpPath> paths() {
        return List.of(
            new HttpPath(
                Method.POST,
                "/wait2",
                (request) -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new HttpBody("DONE SLEEPING2".getBytes(UTF_8), UTF_8);
                }
            ),
            new HttpPath(
                Method.POST,
                "/wait",
                (request) -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new HttpBody("DONE SLEEPING".getBytes(UTF_8), UTF_8);
                }
            )
        );
    }

    @Test
    void sendMultipleRequestsOnSameConnection() throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        var before = LocalTime.now();
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString("LOREM"))
            .uri(new URI("http://127.0.0.1/wait"))
            .version(Version.HTTP_1_1)
            .build();

        HttpRequest httpRequest2 = HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString("LOREM"))
            .uri(new URI("http://127.0.0.1/wait2"))
            .version(Version.HTTP_1_1)
            .build();

        CompletableFuture<HttpResponse<String>> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return httpClient.send(httpRequest, BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<HttpResponse<String>> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                return httpClient.send(httpRequest2, BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture.allOf(future1, future2).join();

        var after = LocalTime.now();
        Assertions.assertEquals("DONE SLEEPING", future1.get().body());
        Assertions.assertEquals("DONE SLEEPING2", future2.get().body());
//        Assertions.assertTrue(Duration.between(before, after).toSeconds() < 3);
    }

}
