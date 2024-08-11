package dev.rifaii.http;

import dev.rifaii.http.exception.RequestParsingException;
import dev.rifaii.http.exception.ServerException;
import dev.rifaii.http.exception.ServerExceptionHandler;
import dev.rifaii.http.exception.UnsupportedProtocolException;
import dev.rifaii.http.spec.HttpHeader;
import dev.rifaii.http.spec.Method;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static dev.rifaii.http.spec.HttpHeader.CONTENT_LENGTH;
import static dev.rifaii.http.spec.HttpStatusCode.OK;
import static dev.rifaii.http.util.HttpResponseConstructor.constructHttpResponse;
import static java.lang.System.Logger.Level.*;

public class HttpServer {

    private static final String QUERY_PARAMS_START_PREFIX = "?";
    private static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";

    private final int port;
    private final System.Logger LOGGER = System.getLogger(HttpServer.class.getName());

    public HttpServer() {
        this(80);
    }

    public HttpServer(int port) {
        this.port = port;
    }

    public void startListening() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            LOGGER.log(INFO, "Server listening on port " + this.port);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.log(INFO, "connection received from " + clientSocket.getInetAddress());
                    CompletableFuture.runAsync(() -> handleConnection(clientSocket));
                } catch (Exception e) {
                    LOGGER.log(ERROR, "err");
                }
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        try {
            boolean keepReading = true;
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

            do {
                String firstLine = in.readLine();
                String[] rlTokens = firstLine.split(" ");
                Method method = Method.valueOf(rlTokens[0]);
                if (!SUPPORTED_HTTP_VERSION.equals(rlTokens[2])) {
                    throw new UnsupportedProtocolException();
                }

                Map<String, String> headers = new HashMap<>();
                String line = in.readLine();
                while (line != null && !line.isEmpty()) {
                    int colonIdx = line.indexOf(":");

                    if (colonIdx == -1) {
                        throw new RequestParsingException("Failed to parse header on line " + line);
                    }
                    headers.put(line.substring(0, colonIdx), line.substring(colonIdx + 1).trim());
                    line = in.readLine();
                }

                int bodyLength = Optional.ofNullable(headers.get(CONTENT_LENGTH.getHeaderName())).map(Integer::parseInt).orElse(0);
                char[] body;
                if (bodyLength != 0) {
                    body = new char[bodyLength];
                    in.read(body);
                }


                String fullPath = rlTokens[1];
                String path;
                var queryParams = new HashMap<String, String>();

                if (fullPath.contains(QUERY_PARAMS_START_PREFIX)) {
                    int queryParamsPrefixIndex = fullPath.indexOf(QUERY_PARAMS_START_PREFIX);
                    path = fullPath.substring(0, queryParamsPrefixIndex);
                    String[] queryParamTokens = fullPath.substring(queryParamsPrefixIndex + 1).split("&");
                    for (String queryParamToken : queryParamTokens) {
                        String[] queryParamKeyValue = queryParamToken.split("=");
                        queryParams.put(queryParamKeyValue[0], queryParamKeyValue[1]);
                    }
                } else {
                    path = fullPath;
                }

                var request = new HttpRequestImpl(
                        method,
                        path,
                        fullPath,
                        headers,
                        queryParams
                );

                boolean closeConnection = "close".equals(request.getHeader(HttpHeader.CONNECTION.getHeaderName()));

                if (closeConnection)
                    keepReading = false;

                CompletableFuture.runAsync(() -> {
                    try {
                        Map<String, String> responseHeaders = new HashMap<>();
                        headers.put(HttpHeader.CONTENT_TYPE.getHeaderName(), "text/plain");
                        String response = constructHttpResponse(
                                OK,
                                responseHeaders,
                                "TEST"
                        );

                        var out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
                        out.write(response);
                        out.flush();

                        if (closeConnection) {
                            clientSocket.getOutputStream().close();
                            clientSocket.close();

                        } else {
                            clientSocket.setKeepAlive(true);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
            } while (keepReading);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            ServerExceptionHandler.handle(clientSocket);
        } catch (Exception e) {
            System.out.printf("Global exception (%s): %s%n", e.getClass().getName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
