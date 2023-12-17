package com.github.westsi.browser.layout;

import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;

import java.awt.*;
import java.util.ArrayList;

public class DocumentLayout implements ILayoutElement {

    private HTMLElement node;
    private ILayoutElement parent;
    private ArrayList<ILayoutElement> children;
    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();

    public DocumentLayout(HTMLElement node) {
        this.node = node;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    @Override
    public void layout() {
        ILayoutElement child = new BlockLayout(this.node, this, null);
        this.children.add(child);
        child.layout();
        this.displayList = child.getDisplayList();
    }

    @Override
    public ArrayList<Pair<Point, StyledString>> getDisplayList() {
        return this.displayList;
    }

    @Override
    public HTMLElement getNode() {
        return this.node;
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
        return this.children;
    }
}
