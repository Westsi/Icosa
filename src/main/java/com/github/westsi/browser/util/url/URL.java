package com.github.westsi.browser.util.url;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class URL {
    private final String url;
    private final URLType urlType;
    public URL(String url) {
        this.url = url;
        urlType = URLType.getFromURLPrefix(url);
    }

    public String Request() throws URISyntaxException, IOException, InterruptedException {
        switch (this.urlType) {
            case File:
                return GetFileContents();
            case DATA:
                return GetDataUrlContents();
            default:
                HttpRequest request = HttpRequest.newBuilder().uri(new URI(this.url)).GET().timeout(Duration.of(10, SECONDS)).setHeader("User-Agent", "Icosa/1.0.0.0 (Xonize)").build();
                HttpResponse<String> response = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build().send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
        }
    }
    public String GetFileContents() {
        // Can be hardcoded because it is guaranteed that if this point is reached the URL starts with "file:///"
        Path filepath = Path.of(this.url.substring(8));
        String contents;
        try {
            contents = Files.readString(filepath);
        } catch (IOException e) {
            contents = "<html><body><h1>Non-existent filepath</h1></body></html>";
        }
        return contents;
    }

    /**
     * At the moment, assumes that the data URL will always be text/html
     */
    public String GetDataUrlContents() {
        // TODO: implement
        // Form of data:MIMETYPE,CONTENTS
        // First colon to first comma is MIMETYPE
        // Everything after first comma is CONTENTS
        String contents;
        try {
            contents = this.url.split(",", 2)[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "<html><body><h1>Malformed data URL</h1></body></html>";
        }
        return contents;
    }

    @Override
    public String toString() {
        return url;
    }
}
