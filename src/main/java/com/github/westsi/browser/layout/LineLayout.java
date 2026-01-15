package com.github.westsi.browser.layout;

import com.github.westsi.browser.util.html.HTMLElement;

import java.util.ArrayList;

public class LineLayout {
    private HTMLElement node;
    private ILayoutElement parent;
    private ILayoutElement _previous;
    private ArrayList<ILayoutElement> children;
    public LineLayout(HTMLElement node, ILayoutElement parent, ILayoutElement previous) {
        this.node = node;
        this.parent = parent;
        this._previous = previous;
        this.children = new ArrayList<>();
    }
}
