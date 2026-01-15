package com.github.westsi.browser.layout;

import com.github.westsi.browser.util.html.HTMLElement;

import java.util.ArrayList;

public class TextLayout {
    private HTMLElement node;
    private ILayoutElement parent;
    private ILayoutElement _previous;
    private String word;
    private ArrayList<ILayoutElement> children;
    public TextLayout(HTMLElement node, String word, ILayoutElement parent, ILayoutElement previous) {
        this.node = node;
        this.parent = parent;
        this._previous = previous;
        this.children = new ArrayList<>();
        this.word = word;
    }
}
