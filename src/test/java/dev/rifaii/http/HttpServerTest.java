package dev.rifaii.http;

import dev.rifaii.http.spec.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    HttpServer server = new HttpServer();

    @Test
    void parseRequest_ParsesRequestSuccessfully_GivenValidInput() throws IOException {
        StringReader stringReader = new StringReader(
            """
            POST / HTTP/1.1\r\n\
            Connection: keep-alive\r\n
            \r\n
            Lorem Ipsum
            """
        );

        HttpRequest httpRequest = server.parseRequest(new BufferedReader(stringReader));

        String connectionHeaderVal = httpRequest.getHeader("Connection");

        assertEquals("/", httpRequest.getPath());
        assertEquals("/", httpRequest.getFullPath());
        assertEquals(Method.POST, httpRequest.getMethod());
        assertEquals("keep-alive", connectionHeaderVal);
        assertEquals("Lorem Ipsum".getBytes(), httpRequest.getBody());
    }
}
