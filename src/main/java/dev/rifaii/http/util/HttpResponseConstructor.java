package dev.rifaii.http.util;

import dev.rifaii.http.spec.HttpStatusCode;

import java.util.Collections;
import java.util.Map;

public class HttpResponseConstructor {

    private static final String DEFAULT_HTTP_PROTOCOL = "HTTP/1.1";
    private static final String CR_NL = "\r\n";
    private static final StringBuilder stringBuilder = new StringBuilder();

    public static String constructHttpResponse(HttpStatusCode sc) {
        return constructHttpResponse(sc, Collections.emptyMap(), null);
    }

    public static String constructHttpResponse(HttpStatusCode sc, Map<String, String> headers, String body) {
        return constructHttpResponse(DEFAULT_HTTP_PROTOCOL, sc, headers, body);
    }

    public static String constructHttpResponse(String httpProtocol, HttpStatusCode sc, Map<String, String> headers, String body) {
        //e.g: "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nBody"
        stringBuilder.append(httpProtocol)
                .append(" ")
                .append("%s %s".formatted(sc.getCode(), sc.getDescription()))
                .append(CR_NL)

                .append(mapToHeaders(headers))
                .append(CR_NL)
                .append(CR_NL)

                .append(body);

        String str = stringBuilder.toString();
        stringBuilder.setLength(0);
        return str;
    }

    private static String mapToHeaders(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, val) -> {
            sb.append("%s: %s".formatted(key, val));
        });
        return sb.toString();
    }
}
