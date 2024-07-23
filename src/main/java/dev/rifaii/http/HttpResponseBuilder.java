package dev.rifaii.http;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpResponseBuilder {

    HttpResponse build(OutputStream out);

}
