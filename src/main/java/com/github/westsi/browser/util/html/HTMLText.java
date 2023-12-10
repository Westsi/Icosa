package com.github.westsi.browser.util.html;

import java.util.ArrayList;

/**
 * Represents a textual node in the DOM tree.
 * @author Westsi
 * @version %I%
 */
public class HTMLText implements HTMLElement {
    private String text;
    private HTMLElement parent;
    private ArrayList<HTMLElement> children;

    public HTMLText(String text, HTMLElement parent) {
        this.text = text;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        return text;
    }

    public String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }

    @Override
    public void addChild(HTMLElement child) {
        children.add(child);
    }
    @Override
    public HTMLElement getParent() {
        return this.parent;
    }

    @Override
    public ArrayList<HTMLElement> getChildren() {
        return this.children;
    }
}
