package dev.rifaii.http.exception;

import java.io.OutputStream;

public class MalformedRequest extends RuntimeException {
    private final OutputStream outputStream;

    public MalformedRequest(OutputStream outputStream) {
        super("Malformed Request");
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
