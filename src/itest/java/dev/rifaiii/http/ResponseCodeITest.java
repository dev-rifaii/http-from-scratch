package dev.rifaiii.http;

import dev.rifaii.http.path.HttpBody;
import dev.rifaii.http.path.HttpPath;
import dev.rifaii.http.spec.HttpStatusCode;
import dev.rifaii.http.spec.Method;
import dev.rifaiii.ITestBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.stream.Stream;

import static dev.rifaii.http.spec.HttpStatusCode.HTTP_VERSION_NOT_SUPPORTED;
import static dev.rifaii.http.spec.HttpStatusCode.METHOD_NOT_ALLOWED;
import static dev.rifaii.http.spec.HttpStatusCode.NOT_FOUND;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ResponseCodeITest extends ITestBase {

    @Override
    protected List<HttpPath> paths() {
        return List.of(new HttpPath(Method.GET, "/", request -> new HttpBody("TEST".getBytes(), UTF_8)));
    }

    @ParameterizedTest
    @MethodSource("responseCodesTestArgumentsProvider")
    void responseCodeTest(
        String path,
        Method httpMethod,
        HttpStatusCode expectedStatusCode
    ) throws URISyntaxException, IOException, InterruptedException {
        var request = buildRequest(path, httpMethod);
        HttpResponse<Void> response = httpClient.send(request, BodyHandlers.discarding());
        assertEquals(expectedStatusCode.getCode(), response.statusCode());
    }

    static Stream<Arguments> responseCodesTestArgumentsProvider() {
        return Stream.of(
            arguments("/", Method.POST, METHOD_NOT_ALLOWED),
            arguments("/rand", Method.GET, NOT_FOUND)
        );
    }

    @Disabled("Java HTTP client is defaulting tp HTTP 1.1 despite setting the version to 2 in the request, might move to apache http client")
    @Test
    void unsupportedHttpVersionResponseCodeTesT() throws URISyntaxException, IOException, InterruptedException {
        var request = buildRequest("/", Method.GET, null, Version.HTTP_2);
        HttpResponse<Void> response = httpClient.send(request, BodyHandlers.discarding());
        assertEquals(HTTP_VERSION_NOT_SUPPORTED.getCode(), response.statusCode());
    }

    //==================================================UTIL METHODS==========================================================\\

    HttpRequest buildRequest(String path, Method httpMethod) throws URISyntaxException {
        return buildRequest(path, httpMethod, null, Version.HTTP_1_1);
    }

    HttpRequest buildRequest(String path, Method httpMethod, String body) throws URISyntaxException {
        return buildRequest(path, httpMethod, body, Version.HTTP_1_1);
    }

    HttpRequest buildRequest(String path, Method httpMethod, String body, Version httpVersion) throws URISyntaxException {
        switch (httpMethod) {
            case POST -> {
                return  HttpRequest.newBuilder()
                    .POST(body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body))
                    .uri(new URI(DEFAULT_SERVER_HOST + path))
                    .version(httpVersion)
                    .build();
            }
            case GET -> {
                return  HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(DEFAULT_SERVER_HOST + path))
                    .version(httpVersion)
                    .build();
            }
            default -> throw new RuntimeException("UNSUPPORTED METHOD");
        }
    }
}