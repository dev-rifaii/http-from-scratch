package dev.rifaii.http;

import dev.rifaii.http.spec.HttpStatusCode;

import java.io.OutputStream;

public interface HttpResponse {

    HttpStatusCode getStatusCode();

    void setStatusCode(HttpStatusCode status);

    OutputStream getOutputStream();

}
