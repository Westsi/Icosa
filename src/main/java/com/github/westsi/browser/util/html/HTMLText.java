package com.github.westsi.browser.util.html;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a textual node in the DOM tree.
 * @author Westsi
 * @version %I%
 */
public class HTMLText implements HTMLElement {
    private String text;
    private HTMLElement parent;
    private ArrayList<HTMLElement> children;
    private HashMap<String, String> style = new HashMap<>();

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

    public void setChildren(ArrayList<HTMLElement> newChildren) {
        this.children = newChildren;
    }

    @Override
    public void setStyle(HashMap<String, String> styleMap) {
        this.style = styleMap;
    }

    @Override
    public void addStyle(String key, String value) {
        this.style.put(key, value);
    }

    @Override
    public HashMap<String, String> getStyle() {
        return this.style;
    }
}
