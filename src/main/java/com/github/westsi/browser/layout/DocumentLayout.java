package com.github.westsi.browser.layout;

import com.github.westsi.browser.util.html.HTMLElement;

import java.util.ArrayList;

public class DocumentLayout implements ILayoutElement {

    HTMLElement node;
    ILayoutElement parent;
    ArrayList<ILayoutElement> children;

    public DocumentLayout(HTMLElement node) {
        this.node = node;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public void layout() {

    }

    @Override
    public HTMLElement getNode() {
        return null;
    }

    @Override
    public ILayoutElement getParent() {
        return null;
    }

    @Override
    public ILayoutElement getPrevious() {
        return null;
    }

    @Override
    public ArrayList<ILayoutElement> getChildren() {
        return null;
    }
}
