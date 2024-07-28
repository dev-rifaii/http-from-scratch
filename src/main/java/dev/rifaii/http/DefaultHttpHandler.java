package dev.rifaii.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static dev.rifaii.http.spec.HttpStatusCode.OK;
import static dev.rifaii.http.util.HttpResponseConstructor.constructHttpResponse;

public class DefaultHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            var out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
            out.write(constructHttpResponse(
                OK,
                Map.of("Content-Type", "text/plain"),
                "TEST"
            ));
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
