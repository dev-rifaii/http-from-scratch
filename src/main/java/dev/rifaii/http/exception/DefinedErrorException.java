package dev.rifaii.http.exception;

import dev.rifaii.http.spec.HttpStatusCode;

public class DefinedErrorException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public DefinedErrorException(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
