package com.github.westsi.browser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class URL {
    private final String url;
    public URL(String url) {
        this.url = url;
    }

    public String Request() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(this.url)).GET().timeout(Duration.of(10, SECONDS)).build();
        HttpResponse<String> response = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public String toString() {
        return url;
    }
}
