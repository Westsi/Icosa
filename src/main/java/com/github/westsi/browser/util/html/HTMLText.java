package com.github.westsi.browser.util.html;

/**
 * @author Westsi
 * @version %I%
 */
public class HTMLText implements HTMLElement {
    public String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }

    private String text;

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
