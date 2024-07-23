package dev.rifaii.http;

import java.io.IOException;
import java.net.Socket;

public interface HttpRequestParser {

    HttpRequest parse(Socket clientSocket) throws IOException;

}
