package com.github.westsi.browser.util.html;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Westsi
 * @version %I%
 */

// Where browser.engineering uses Element, we use HTMLTag
public class HTMLTag implements HTMLElement {
    private final String tag;
    private HashMap<String, String> attributes = new HashMap<>();
    private HTMLElement parent;
    private ArrayList<HTMLElement> children;
    public HashMap<String, String> style = new HashMap<>();

    public HTMLTag(String tag, HTMLElement parent) {
        this.tag = tag;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public HTMLTag(String tag, HTMLElement parent, HashMap<String, String> attributes) {
        if (tag.contains("pre")) {
            System.out.println("TAG="+tag);
        }
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
