package dev.rifaii.http;

import dev.rifaii.http.exception.RequestParsingException;
import dev.rifaii.http.spec.HttpHeader;
import dev.rifaii.http.spec.Method;
import dev.rifaii.http.exception.UnsupportedProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DefaultHttpRequestParser implements HttpRequestParser {

    private static final Set<String> SUPPORTED_PROTOCOLS = Set.of("HTTP/1.1");

    @Override
    public HttpRequest parse(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

        String[] rlTokens = in.readLine().split(" ");
        Method method = Method.valueOf(rlTokens[0]);
        if (!SUPPORTED_PROTOCOLS.contains(rlTokens[2])) {
            throw new UnsupportedProtocolException();
        }

        Map<String, String> headers = new HashMap<>();
        String line = in.readLine();
        while (line != null && !line.equals("\n") && !line.equals("\r\n") && !line.equals("\r") && !line.equals("")) {
            int colonIdx = line.indexOf(":");

            if (colonIdx == -1) {
                throw new RequestParsingException("Failed to parse header on line " + line);
            }
            headers.put(line.substring(0, colonIdx), line.substring(colonIdx + 1, line.length()).trim());
            line = in.readLine();
        }

//        String body = null;
//        if (headers.get(HttpHeader.CONTENT_LENGTH.getHeaderName()) != null) {
//            var sb = new StringBuilder();
//
//            while (scanner.hasNextLine()) {
//                sb.append(scanner.nextLine());
//            }
//
//            body = sb.toString();
//        }

        return new HttpRequestImpl(method, rlTokens[1], Collections.emptyMap(), null);
    }

//    @Override
//    public HttpRequest parse(Socket clientSocket) throws IOException {
//        Scanner scanner = new Scanner(clientSocket.getInputStream(), StandardCharsets.UTF_8);
//
//        String[] rlTokens = scanner.nextLine().split(" ");
//        Method method = Method.valueOf(rlTokens[0]);
//
//        if (!SUPPORTED_PROTOCOLS.contains(rlTokens[2])) {
//            throw new UnsupportedProtocolException();
//        }
//
//        Map<String, String> headers = new HashMap<>();
//        String line = scanner.nextLine();
//        while (line != null && !line.equals("\n") && !line.equals("\r\n") && !line.equals("\r") && !line.equals("")) {
//            int colonIdx = line.indexOf(":");
//
//            if (colonIdx == -1) {
//                throw new RequestParsingException("Failed to parse header on line " + line);
//            }
//            headers.put(line.substring(0, colonIdx), line.substring(colonIdx + 1, line.length()).trim());
//            line = scanner.nextLine();
//        }
//
//        String body = null;
//        if (headers.get(HttpHeader.CONTENT_LENGTH.getHeaderName()) != null) {
//            var sb = new StringBuilder();
//
//            while (scanner.hasNextLine()) {
//                sb.append(scanner.nextLine());
//            }
//
//            body = sb.toString();
//        }
//
//        return new HttpRequestImpl(method, rlTokens[1], Collections.emptyMap(), body);
//    }

}
