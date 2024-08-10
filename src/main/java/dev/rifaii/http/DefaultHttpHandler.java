package dev.rifaii.http;

import dev.rifaii.http.spec.HttpHeader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static dev.rifaii.http.spec.HttpStatusCode.OK;
import static dev.rifaii.http.util.HttpResponseConstructor.constructHttpResponse;

public class DefaultHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            var out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeader.CONTENT_TYPE.getHeaderName(), "text/plain");

            out.write(constructHttpResponse(
                OK,
                headers,
                "TEST"
            ));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
