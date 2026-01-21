package com.github.westsi.browser.layout;

import com.github.westsi.browser.layout.drawn.IDrawCmd;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;

import static com.github.westsi.browser.BrowserTab.HSTEP;
import static com.github.westsi.browser.BrowserTab.VSTEP;
import static com.github.westsi.browser.Browser.WIDTH;

import java.awt.*;
import java.util.ArrayList;

public class DocumentLayout implements ILayoutElement {

    private HTMLElement node;
    private ILayoutElement parent;
    private ArrayList<ILayoutElement> children;
    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
    private Integer x, y, width, height, cursorX, cursorY;

    public DocumentLayout(HTMLElement node) {
        this.node = node;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    @Override
    public void layout() {
//
//        self.width = WIDTH - 2*HSTEP
//        self.x = HSTEP
//        self.y = VSTEP
//        child.layout()
//        self.height = child.height

        ILayoutElement child = new BlockLayout(this.node, this, null);
        this.children.add(child);
        this.width = WIDTH - 2*HSTEP;
        this.x = HSTEP;
        this.y = VSTEP;
        child.layout();
        this.height = child.getHeight();
    }

    public ArrayList<IDrawCmd> paint() {
        return new ArrayList<>();
    }

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

    @Override
    public Integer getX() {
        return this.x;
    }

    @Override
    public Integer getY() {
        return this.y;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }
}
