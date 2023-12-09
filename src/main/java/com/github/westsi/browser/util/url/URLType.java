package com.github.westsi.browser.util.url;

public enum URLType {
    File("file:///"),
    HTTP("http://"),
    HTTPS("https://"),
    DATA("data:"),
    VIEW_SOURCE("view-source:");

    public final String protocol;
    URLType(String protocol) {
        this.protocol = protocol;
    }

    public static URLType getFromURLPrefix(String url) {
        for (URLType u : values()) {
            if (url.startsWith(u.protocol)) return u;
        }
        return null;
    }

}
