package dev.rifaii.http.exception;

public class PathNotFoundException extends RuntimeException {

    public PathNotFoundException(String message) {
        super(message);
    }
}
