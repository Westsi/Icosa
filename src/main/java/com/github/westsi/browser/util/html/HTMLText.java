package com.github.westsi.browser.util.html;

public class HTMLText implements HTMLElement {
    public String getText() {
        return text;
    }

    private final String text;

    public HTMLText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "HTMLText{" +
                "text='" + text + '\'' +
                '}';
    }
}
