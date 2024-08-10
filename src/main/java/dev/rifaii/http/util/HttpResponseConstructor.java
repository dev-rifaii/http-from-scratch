package dev.rifaii.http.util;

import dev.rifaii.http.spec.HttpHeader;
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
        headers.put(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.length() + CR_NL.length()));
        String headersMap = mapToHeaders(headers);

        stringBuilder.append(httpProtocol)
                .append(" ")
                .append("%s %s".formatted(sc.getCode(), sc.getDescription()))
                .append(CR_NL)

                .append(headersMap)
                .append(CR_NL)

                .append(body)
                .append(CR_NL)
        ;

        String str = stringBuilder.toString();
        stringBuilder.setLength(0);
        return str;
    }

    private static String mapToHeaders(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, val) -> {
            sb.append("%s: %s%s".formatted(key, val, CR_NL));
        });
        return sb.toString();
    }
}
