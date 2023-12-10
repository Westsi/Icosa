package com.github.westsi.browser.util.html;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Westsi
 * @version %I%
 */
public class HTMLTag implements HTMLElement {
    private final String tag;
    private HashMap<String, String> attributes = new HashMap<>();
    private HTMLElement parent;
    private ArrayList<HTMLElement> children;

    public HTMLTag(String tag, HTMLElement parent) {
        this.tag = tag;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public HTMLTag(String tag, HTMLElement parent, HashMap<String, String> attributes) {
        this.tag = tag;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "<" + this.tag + this.attributes + ">";
    }

    @Override
    public HTMLElement getParent() {
        return this.parent;
    }

    @Override
    public ArrayList<HTMLElement> getChildren() {
        return this.children;
    }

    @Override
    public void addChild(HTMLElement child) {
        this.children.add(child);
    }

    public String getTag() {
        return tag;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void addAttribute(String attr, String val) {
        attributes.put(attr, val);
    }
}
