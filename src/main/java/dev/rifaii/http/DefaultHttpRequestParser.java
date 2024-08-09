package dev.rifaii.http;

import dev.rifaii.http.exception.RequestParsingException;
import dev.rifaii.http.spec.HttpHeader;
import dev.rifaii.http.spec.Method;
import dev.rifaii.http.exception.UnsupportedProtocolException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static dev.rifaii.http.spec.HttpHeader.CONTENT_LENGTH;
import static java.lang.Integer.parseInt;

public class DefaultHttpRequestParser implements HttpRequestParser {

    private static final String QUERY_PARAMS_START_PREFIX = "?";
    private static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";

    @Override
    public HttpRequest parse(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

        String[] rlTokens = in.readLine().split(" ");
        Method method = Method.valueOf(rlTokens[0]);
        if (!SUPPORTED_HTTP_VERSION.equals(rlTokens[2])) {
            throw new UnsupportedProtocolException();
        }

        Map<String, String> headers = new HashMap<>();
        String line = in.readLine();
        while (line != null && !line.isEmpty()) {
            int colonIdx = line.indexOf(":");

            if (colonIdx == -1) {
                throw new RequestParsingException("Failed to parse header on line " + line);
            }
            headers.put(line.substring(0, colonIdx), line.substring(colonIdx + 1).trim());
            line = in.readLine();
        }

        int bodyLength = Optional.ofNullable(headers.get(CONTENT_LENGTH.getHeaderName())).map(Integer::parseInt).orElse(0);
        char[] body;
        if (bodyLength != 0) {
            body = new char[bodyLength];
            in.read(body);
        }


        String fullPath = rlTokens[1];
        String path;
        var queryParams = new HashMap<String, String>();

        if (fullPath.contains(QUERY_PARAMS_START_PREFIX)) {
            int queryParamsPrefixIndex = fullPath.indexOf(QUERY_PARAMS_START_PREFIX);
            path = fullPath.substring(0, queryParamsPrefixIndex);
            String[] queryParamTokens = fullPath.substring(queryParamsPrefixIndex + 1).split("&");
            for (String queryParamToken : queryParamTokens) {
                String[] queryParamKeyValue = queryParamToken.split("=");
                queryParams.put(queryParamKeyValue[0], queryParamKeyValue[1]);
            }
        } else {
            path = fullPath;
        }

        return new HttpRequestImpl(
                method,
                path,
                fullPath,
                headers,
                queryParams
        );
    }
}
