package dev.rifaii.http;

import dev.rifaii.http.spec.HttpHeader;
import dev.rifaii.http.spec.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpServerTest {

    HttpServer server = new HttpServer();

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
