package dev.rifaiii.http;

import dev.rifaii.http.path.HttpBody;
import dev.rifaii.http.path.HttpPath;
import dev.rifaii.http.spec.Method;
import dev.rifaiii.ITestBase;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.DefaultConnectingIOReactor;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PipeliningITest extends ITestBase {

    protected List<HttpPath> paths() {
        return List.of(
            new HttpPath(
                Method.POST,
                "/wait",
                (request) -> {
                    String waitTime = request.getQueryParams().get("time");
                    try {
                        Thread.sleep(Long.parseLong(waitTime));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new HttpBody("%s".formatted(waitTime).getBytes(UTF_8), UTF_8);
                }
            )
        );
    }

    //Apache HTTP client is used here since pipelining requests with it is easier
    @RepeatedTest(10)
    void pipelineRequests() throws InterruptedException, ExecutionException, IOException, TimeoutException {
        final MinimalHttpAsyncClient client = HttpAsyncClients.createMinimal(
            H2Config.DEFAULT,
            Http1Config.DEFAULT,
            IOReactorConfig.DEFAULT,
            PoolingAsyncClientConnectionManagerBuilder.create().setMaxConnPerRoute(1).build()
        );

        client.start();

        final HttpHost target = new HttpHost("127.0.0.1");
        final Future<AsyncClientEndpoint> leaseFuture = client.lease(target, null);
        final AsyncClientEndpoint endpoint = leaseFuture.get(30, TimeUnit.SECONDS);
        try {
            final String[] requestUris = new String[]{ waitXPath(2000), waitXPath(1000) };

            final CountDownLatch latch = new CountDownLatch(requestUris.length);
            for (final String requestUri : requestUris) {
                final SimpleHttpRequest request = SimpleRequestBuilder.post()
                    .setHttpHost(target)
                    .setPath(requestUri)
                    .build();

                System.out.println("Executing request " + request);
                endpoint.execute(
                    SimpleRequestProducer.create(request),
                    SimpleResponseConsumer.create(),
                    new FutureCallback<>() {

                        @Override
                        public void completed(final SimpleHttpResponse response) {
                            latch.countDown();
                            System.out.println(request + "->" + new StatusLine(response));
                            System.out.println(response.getBody().getBodyText());
                            assertEquals(requestUri.substring(11), response.getBodyText());
                        }

                        @Override
                        public void failed(final Exception ex) {
                            latch.countDown();
                            System.out.println(request + "->" + ex);
                            throw new RuntimeException("Request failed", ex);
                        }

                        @Override
                        public void cancelled() {
                            latch.countDown();
                            System.out.println(request + " cancelled");
                            throw new RuntimeException("Request cancelled");
                        }

                    }
                );
            }
            latch.await();
        } finally {
            endpoint.releaseAndReuse();
        }

        System.out.println("Shutting down");
        client.close(CloseMode.GRACEFUL);
    }

    private static String waitXPath(long millis) {
        return "/wait?time=%s".formatted(millis);
    }
}
