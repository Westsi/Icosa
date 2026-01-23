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
import java.util.Arrays;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Represents a URL and its type as specified in <code>URLType</code>.
 * @author Westsi
 * @version %I%
 * @see URLType
 */
public class URL {
    private final String url;

    public URLType getUrlType() {
        return urlType;
    }

    private final URLType urlType;
    public URL(String url) {
        if (isSearchQuery(url)) {
            this.url = "https://duckduckgo.com/html/?q=" + url;
        } else {
            this.url = url;
        }
        urlType = URLType.getFromURLPrefix(this.url);
    }

    public String Request() throws URISyntaxException, IOException, InterruptedException {
        switch (this.urlType) {
            case FILE:
                return GetFileContents();
            case DATA:
                return GetDataUrlContents();
            case ICOSA_INTERNAL:
                return GetIcosaInternalUrlContents();
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
     * @return The contents of the data url, from the "," after the mimetype onwards or an error string if not present.
     */
    public String GetDataUrlContents() {
        String contents;
        try {
            contents = this.url.split(",", 2)[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "<html><body><h1>Malformed data URL</h1></body></html>";
        }
        return contents;
    }

    public String GetIcosaInternalUrlContents() {
        // Can be hardcoded because it is guaranteed that if this point is reached the URL starts with "icosa://"
        String intUrl = this.url.substring(8);
        switch (intUrl) {
            case "home":
                return GetIcosaHomeContents();
        }
        return "";
    }

    public String GetIcosaHomeContents() {
        return "<html><body><h1>Welcome to Icosa!</h1><h4>Enter a webpage URL in the bar above...</h4></body></html>";
    }

    public boolean isSearchQuery(String text) {
        URLType a = URLType.getFromURLPrefix(text);
        boolean b = a == null;
//        System.out.println(a + " "+ b);
        return b;
    }

//    def resolve(self, url):
//        if "://" in url: return URL(url)
//        if not url.startswith("/"):
//            dir, _ = self.path.rsplit("/", 1)
//            while url.startswith("../"):
//                _, url = url.split("/", 1)
//                if "/" in dir:
//                    dir, _ = dir.rsplit("/", 1)
//            url = dir + "/" + url
//        if url.startswith("//"):
//                return URL(self.scheme + ":" + url)
//        else:
//                return URL(self.scheme + "://" + self.host + \
//            ":" + str(self.port) + url)

    public URL resolve(String nurl) {
//        System.out.println("nurl " + nurl);
        if (nurl.contains("://")) {return new URL(nurl);}
        if (!nurl.startsWith("/")) {
//            System.out.println("NOT START WITH SLASH");
            String dir = splitAtLastSlash(this.url)[0];
//            System.out.println(Arrays.toString(splitAtLastSlash(this.url)));
            while (nurl.startsWith("../")) {
                nurl = nurl.split("/", 2)[1];
                if (dir.contains("/")) {
                    dir = splitAtLastSlash(dir)[0];
                }
            }
            nurl = dir + "/" + nurl;
            return new URL(nurl);
        }
//        System.out.println("nurl2 " + nurl);
        if (nurl.startsWith("//")) {
            return new URL("https:" + nurl);
        } else if (nurl.startsWith("/")) {
            // to make sure the last slash comes after the last dot, otherwise the splitting stuff fucks up the https://
            String dir = this.url;
            if (this.url.lastIndexOf(".") < this.url.lastIndexOf("/")) {
                dir = splitAtFirstSlashAfterDot(this.url)[0];
            }
            nurl = dir + nurl;
            return new URL(nurl);
        } else {
            return new URL("BROKEN");
        }
    }

    private static String[] splitAtLastSlash(String s) {
        int pos = s.lastIndexOf('/');
        if(pos == -1)
            return new String[] {s};
        return new String[] {s.substring(0, pos), s.substring(pos+1)};
    }

    private static String[] splitAtFirstSlashAfterDot(String s) {
        int pos = s.lastIndexOf(".");
        if (pos == -1) {
            // PANIC!!!
            return new String[] {""};
        }
        String[] ss = s.substring(pos).split("/", 2);
        ss[0] = s.substring(0, pos) + ss[0];
        return ss;
    }

    @Override
    public String toString() {
        return url;
    }
}
