package com.github.westsi.browser.layout;

import com.github.westsi.browser.layout.drawn.IDrawCmd;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.Quartet;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;

import java.awt.*;
import java.util.ArrayList;

public class LineLayout implements ILayoutElement {
    private HTMLElement node;
    private ILayoutElement parent;
    private ILayoutElement _previous;
    private ArrayList<ILayoutElement> children;
    private Integer width, height, x, y;
    public LineLayout(HTMLElement node, ILayoutElement parent, ILayoutElement previous) {
        this.node = node;
        this.parent = parent;
        this._previous = previous;
        this.children = new ArrayList<>();
        this.width = 0;
        this.height = 0;
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
        return this._previous;
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

    @Override
    public void layout() {
        /*
            self.width = self.parent.width
            self.x = self.parent.x

            if self.previous:
                self.y = self.previous.y + self.previous.height
            else:
                self.y = self.parent.y
        */
    this.width = this.parent.getWidth();
    this.x = this.parent.getX();
    if (this._previous != null) {
        this.y = this._previous.getY() + this._previous.getHeight();
    } else {
        this.y = this.parent.getY();
    }

    for (ILayoutElement word : this.children) {
        word.layout();
    }

        Integer maxAscent = 0;
        Integer maxDescent = 0;
        for (ILayoutElement _word : this.children) {
            TextLayout word = (TextLayout) _word;
            int lma = new Canvas().getFontMetrics(word.getFont()).getMaxAscent();
            if (lma > maxAscent) {
                maxAscent = lma;
            }
            int lmd = new Canvas().getFontMetrics(word.getFont()).getMaxDescent();
            if (lmd > maxDescent) {
                maxDescent = lmd;
            }
        }
        Integer baseline = (int) (this.y + 1.25 * maxAscent);

        for (ILayoutElement _word : this.children) {
            TextLayout word = (TextLayout) _word;
            word.setY(baseline - new Canvas().getFontMetrics(word.getFont()).getMaxAscent());
        }

        this.height = (int) (1.25 * (maxAscent + maxDescent));

    }

    @Override
    public ArrayList<IDrawCmd> paint() {
        return new ArrayList<>();
    }

}
