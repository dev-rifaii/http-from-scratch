package dev.rifaii.http;

import dev.rifaii.http.exception.RequestParsingException;
import dev.rifaii.http.exception.ServerException;
import dev.rifaii.http.exception.ServerExceptionHandler;
import dev.rifaii.http.exception.UnsupportedProtocolException;
import dev.rifaii.http.path.HttpBody;
import dev.rifaii.http.path.HttpPath;
import dev.rifaii.http.path.DefaultPathRegistry;
import dev.rifaii.http.path.PathRegistry;
import dev.rifaii.http.spec.HttpHeader;
import dev.rifaii.http.spec.Method;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import static dev.rifaii.http.spec.HttpHeader.CONNECTION;
import static dev.rifaii.http.spec.HttpHeader.CONTENT_LENGTH;
import static dev.rifaii.http.spec.HttpStatusCode.OK;
import static dev.rifaii.http.util.HttpResponseConstructor.constructHttpResponse;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.TRACE;
import static java.util.concurrent.CompletableFuture.runAsync;

public class HttpServer {

    private static final String QUERY_PARAMS_START_PREFIX = "?";
    private static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";

    private final System.Logger LOGGER = System.getLogger(HttpServer.class.getName());
    private final ServerSocket serverSocket;
    private final PathRegistry pathRegistry = new DefaultPathRegistry();
    private final Map<Socket, Queue<HttpRequest>> pipeliningPool = new HashMap<>();

    boolean requestInProgress = false;

    public HttpServer() throws IOException {
        this(80, Collections.emptyList());
    }

    public HttpServer(List<HttpPath> paths) throws IOException {
        this(80, paths);
    }

    public HttpServer(int port, List<HttpPath> paths) throws IOException {
         serverSocket = new ServerSocket(port);
         paths.forEach(pathRegistry::register);
    }



    public void startListening() throws IOException {
        var thread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    requestInProgress = true;
                    LOGGER.log(INFO, "connection received from " + clientSocket.getInetAddress());
                    runAsync(() -> handleConnection(clientSocket));
                } catch (Exception e) {
                    LOGGER.log(ERROR, e);
                }
            }
        });
        thread.start();
    }

    private void handleConnection(Socket clientSocket) {
        try {
            boolean keepReading = true;
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

            do {
                HttpRequest request = parseRequest(in);
                if (request == null) {
                    return;
                }

                pipeliningPool.computeIfAbsent(clientSocket, k -> new LinkedList<>()).add(request);

                boolean closeConnection = "close".equalsIgnoreCase(request.getHeader(CONNECTION.getHeaderName()));
                if (closeConnection)
                    keepReading = false;

               runAsync(() -> {
                    try {
                        writeResponse(request, clientSocket, closeConnection);
                        requestInProgress = false;
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
            } while (keepReading);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            ServerExceptionHandler.handle(clientSocket);
        } catch (Exception e) {
            LOGGER.log(ERROR, e);
            throw new RuntimeException(e);
        }
    }

    HttpRequest parseRequest(BufferedReader in) throws IOException {
        String firstLine = in.readLine();

        if (firstLine == null)
            return null;

        String[] rlTokens = firstLine.split(" ");

        String methodStr = rlTokens[0];
        boolean isMethodSupported = Method.isValidMethod(methodStr);
        Method method = isMethodSupported ? Method.valueOf(methodStr) : null;

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
        System.out.println("Successfully parsed headers");

        int bodyLength = Optional.ofNullable(headers.get(CONTENT_LENGTH.getHeaderName())).map(Integer::parseInt).orElse(0);
        char[] body = new char[bodyLength];
        if (bodyLength != 0) {
            in.read(body);
        }
        System.out.println("Successfully parsed body");

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
        System.out.println("Successfully parsed query params");
        System.out.printf("Request is going to %s%n", path);

        return new HttpRequestImpl(
                method,
                path,
                fullPath,
                headers,
                queryParams,
                String.valueOf(body).getBytes()
        );
    }

    void writeResponse(HttpRequest request, Socket clientSocket, boolean closeConnection) throws IOException {
        while (pipeliningPool.get(clientSocket).peek() != request) {
            LOGGER.log(TRACE, "Waiting previous request to finish processing");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put(HttpHeader.CONTENT_TYPE.getHeaderName(), "text/plain");
        HttpBody body = pathRegistry.dispatch(request);
        String response = constructHttpResponse(
            OK,
            responseHeaders,
            new String(body.bytes(), body.charset()) //Temporarily assuming all responses are strings
        );

        System.out.printf("Writing response to client for path %s%n", request.getFullPath());
        var out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        out.write(response);
        out.flush();
        System.out.println("Successfully wrote response to client for path " + request.getFullPath());

        pipeliningPool.get(clientSocket).poll();

        if (closeConnection) {
            clientSocket.getOutputStream().close();
            clientSocket.close();

        } else {
            clientSocket.setKeepAlive(true);
        }
    }

    public void stopListening() throws IOException {
        while (requestInProgress) {}
        serverSocket.close();
        LOGGER.log(INFO, "Server socket closed");
    }
}
