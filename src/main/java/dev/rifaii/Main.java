package dev.rifaii;

import dev.rifaii.http.HttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.startListening();
    }
}