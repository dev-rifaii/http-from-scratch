package dev.rifaii;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            // Connect to the server at 127.0.0.1 on port 80
            Socket socket = new Socket("127.0.0.1", 80);

            // Prepare the first HTTP GET request
            String request1 = "GET / HTTP/1.1\r\n"
                              + "Host: 127.0.0.1\r\n"
                              + "Connection: keep-alive\r\n"
                              + "\r\n";

            // Prepare the second HTTP GET request
            String request2 = "GET /another-path HTTP/1.1\r\n"
                              + "Host: 127.0.0.1\r\n"
                              + "Connection: close\r\n"
                              + "\r\n";

            // Send the first request
            OutputStream out = socket.getOutputStream();
            out.write(request1.getBytes());
            out.flush();

            // Read the response for the first request
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseLine;
            System.out.println("Response for the first request:");
            while ((responseLine = in.readLine()) != null) {
                if (responseLine.isEmpty()) break; // End of headers
                System.out.println(responseLine);
            }

            // Send the second request on the same connection
            out.write(request2.getBytes());
            out.flush();

            // Read the response for the second request
            System.out.println("Response for the second request:");
            while ((responseLine = in.readLine()) != null) {
                if (responseLine.isEmpty()) break; // End of headers
                System.out.println(responseLine);
            }

            // Close the connection
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
