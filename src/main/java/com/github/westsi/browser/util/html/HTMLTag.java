package com.github.westsi.browser.util.html;

import javax.swing.text.html.HTML;
import java.util.HashMap;

public class HTMLTag implements HTMLElement{
    private final String tag;

    private HashMap<HTML.Attribute, String> attributes = new HashMap<>();

    public HTMLTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public HashMap<HTML.Attribute, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "HTMLTag{" +
                "tag='" + tag + '\'' +
                '}';
    }
}
