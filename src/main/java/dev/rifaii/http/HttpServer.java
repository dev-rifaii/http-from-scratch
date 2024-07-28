package dev.rifaii.http;

import dev.rifaii.http.exception.ServerException;
import dev.rifaii.http.exception.ServerExceptionHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        this.requestDispatcher = new RequestDispatcherImpl(Map.of("/", new DefaultHttpHandler()));
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
                if (clientSocket.getInputStream().available() == 0) {
                    LOGGER.log(TRACE, () -> "closing connection due to empty input");
                    clientSocket.close();
                    return;
                }

                HttpRequest request = httpRequestParser.parse(clientSocket);
                HttpResponse response = httpResponseBuilder.build(clientSocket.getOutputStream());

                requestDispatcher.dispatch(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                ServerExceptionHandler.handle(clientSocket);
            } catch (Exception e) {
                System.out.println("Global exception" + e.getMessage());
            }
        });
    }
}
