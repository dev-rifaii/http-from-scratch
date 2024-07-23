package dev.rifaii.http;

import dev.rifaii.http.exception.ServerException;
import dev.rifaii.http.exception.ServerExceptionHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static dev.rifaii.http.spec.HttpStatusCode.OK;
import static dev.rifaii.http.util.HttpResponseConstructor.constructHttpResponse;
import static java.lang.System.Logger.Level.*;

public class HttpServer {

    private final int port;
    private final System.Logger LOGGER = System.getLogger(HttpServer.class.getName());

    private final HttpRequestParser httpRequestParser;
    private final HttpResponseBuilder httpResponseBuilder;
    private final RequestDispatcher requestDispatcher;

    public HttpServer(int port) {
        this.port = port;
        this.httpResponseBuilder = new HttpResponseBuilderImpl();
        httpRequestParser = new DefaultHttpRequestParser();
        this.requestDispatcher = new RequestDispatcher(Map.of("/", new DefaultHttpHandler()));
    }

    public void startListening() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            LOGGER.log(INFO, "Server listening on port " + this.port);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.log(INFO, "connection received from " + clientSocket.getInetAddress());
                    handleHttpConnectionAsync(clientSocket);
                } catch (Exception e) {
                    LOGGER.log(ERROR, "err");
                }
            }
        }
    }

    private void handleHttpConnectionAsync(Socket clientSocket) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = httpRequestParser.parse(clientSocket);
                HttpResponse response = httpResponseBuilder.build(clientSocket.getOutputStream());

                requestDispatcher.dispatch(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                ServerExceptionHandler.handle(clientSocket);
            }
        });
    }
}
