package dev.rifaii.http;

import dev.rifaii.http.exception.ServerException;
import dev.rifaii.http.exception.UnsupportedProtocolException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HttpServer {

    private static final Set<String> SUPPORTED_PROTOCOLS = Set.of("HTTP/1.1");
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void startListening() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server listening on port " + this.port);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("connection received from " + clientSocket.getInetAddress());
                    handleHttpConnectionAsync(clientSocket);
                } catch (Exception e) {
                    System.out.println("err");
                }
            }
        }
    }

    private void handleHttpConnectionAsync(Socket clientSocket) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = parseRequest(clientSocket.getInputStream());
                System.out.println("req handled");
                handleHttpResponse(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                try {
                    clientSocket.getOutputStream().write("HTTP/1.1 505 Unsupported Version\r\n\r\n".getBytes());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void handleHttpResponse(Socket clientSocket) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            out.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\n");
            out.flush();
            System.out.println("sent response");
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private HttpRequest parseRequest(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);

        String[] firstLine = scanner.nextLine().split(" ");
        Method method = Method.valueOf(firstLine[0]);
        if (!SUPPORTED_PROTOCOLS.contains(firstLine[2])) {
            throw new UnsupportedProtocolException();
        }

        return new HttpRequestImpl(method, firstLine[1], Collections.emptyMap());
    }
}
