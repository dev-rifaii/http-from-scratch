package dev.rifaii;

import dev.rifaii.http.HttpServer;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(8080);
        httpServer.startListening();
    }
}