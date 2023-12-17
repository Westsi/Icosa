package com.github.westsi.browser.layout;


import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;

import java.awt.*;
import java.util.ArrayList;

public class BlockLayout implements ILayoutElement{
    private HTMLElement node;
    private ILayoutElement parent;
    private ILayoutElement previous;
    private ArrayList<ILayoutElement> children;

    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();

    public BlockLayout(HTMLElement node, ILayoutElement parent, ILayoutElement previous) {
        this.node = node;
        this.parent = parent;
        this.previous = previous;
    }

    @Override
    public void layout() {

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
        return this.parent;
    }

    @Override
    public ILayoutElement getPrevious() {
        return this.previous;
    }

    @Override
    public ArrayList<ILayoutElement> getChildren() {
        return this.children;
    }

}
