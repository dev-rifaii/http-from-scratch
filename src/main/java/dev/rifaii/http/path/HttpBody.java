package dev.rifaii.http.path;

import java.nio.charset.Charset;

public record HttpBody(
    byte[] bytes,
    Charset charset
) {}
