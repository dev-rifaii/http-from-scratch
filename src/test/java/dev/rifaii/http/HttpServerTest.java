package dev.rifaii.http;

import dev.rifaii.http.path.HttpBody;
import dev.rifaii.http.path.HttpPath;
import dev.rifaii.http.spec.HttpHeader;
import dev.rifaii.http.spec.Method;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpServerTest {

    private final HttpServer server = new HttpServer(
        List.of(new HttpPath(Method.POST, "/", request -> new HttpBody("Test".getBytes(UTF_8), UTF_8)))
    );

    public HttpServerTest() throws IOException {
    }

    @Test
    void parseRequest_ParsesRequestSuccessfully_GivenValidInput() throws IOException {
        String request = """
                         POST / HTTP/1.1\r\n\
                         Connection: keep-alive\r\n\
                         Content-Length: 11\r\n\
                         \r\n\
                         Lorem Ipsum\
                         """;

        HttpRequest httpRequest = server.parseRequest(new BufferedReader(new StringReader(request)));

        assertEquals("/", httpRequest.getPath());
        assertEquals("/", httpRequest.getFullPath());
        assertEquals(Method.POST, httpRequest.getMethod());
        assertEquals("keep-alive", httpRequest.getHeader(HttpHeader.CONNECTION.getHeaderName()));
        assertEquals("11", httpRequest.getHeader(HttpHeader.CONTENT_LENGTH.getHeaderName()));
        assertTrue(Arrays.equals("Lorem Ipsum".getBytes(), httpRequest.getBody()));
    }
}
