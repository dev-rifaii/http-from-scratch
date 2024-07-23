package dev.rifaii.http;

import dev.rifaii.http.spec.Method;
import dev.rifaii.http.exception.UnsupportedProtocolException;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;

public class DefaultHttpRequestParser implements HttpRequestParser {

    private static final Set<String> SUPPORTED_PROTOCOLS = Set.of("HTTP/1.1");

    @Override
    public HttpRequest parse(Socket clientSocket) throws IOException {
        Scanner scanner = new Scanner(clientSocket.getInputStream(), StandardCharsets.UTF_8);

        String[] firstLine = scanner.nextLine().split(" ");
        Method method = Method.valueOf(firstLine[0]);
        if (!SUPPORTED_PROTOCOLS.contains(firstLine[2])) {
            throw new UnsupportedProtocolException();
        }

        return new HttpRequestImpl(method, firstLine[1], Collections.emptyMap());
    }

}
