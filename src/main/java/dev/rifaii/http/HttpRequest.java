package dev.rifaii.http;

import dev.rifaii.http.spec.Method;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface HttpRequest {

    String getPath();

    /*
     * Request path but includes query parameters
     */
    String getFullPath();

    Map<String, String> getQueryParams();

    Method getMethod();

    InputStream getInputStream();

    OutputStream getOutputStream();

    Map<String, String> getHeaders();

    String getHeader(String name);

    byte[] getBody();

}
