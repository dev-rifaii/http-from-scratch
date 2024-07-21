package dev.rifaii.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface HttpRequest {

    String getPath();

    Method getMethod();

    InputStream getInputStream();

    OutputStream getOutputStream();

    Map<String, String> getHeaders();
}
