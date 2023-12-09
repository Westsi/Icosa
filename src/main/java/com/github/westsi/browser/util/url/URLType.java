package com.github.westsi.browser.util.url;

/**
 * The type of a URL that can be handled by the browser.
 * Has utility methods for working this out from a URL.
 * @author Westsi
 * @version %I%
 */
public enum URLType {
    FILE("file:///"),
    HTTP("http://"),
    HTTPS("https://"),
    DATA("data:"),
    VIEW_SOURCE("view-source:");

    public final String protocol;
    URLType(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @param url The url whose type needs to be found
     * @return The enum instance for the URLType
     */
    public static URLType getFromURLPrefix(String url) {
        for (URLType u : values()) {
            if (url.startsWith(u.protocol)) return u;
        }
        return null;
    }

}
