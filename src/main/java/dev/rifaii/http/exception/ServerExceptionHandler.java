package dev.rifaii.http.exception;

import java.io.IOException;
import java.net.Socket;

public class ServerExceptionHandler {

    public static void handle(Socket clientSocket) {
        try {
            clientSocket.getOutputStream().write("HTTP/1.1 505 Unsupported Version\r\n\r\n".getBytes());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
