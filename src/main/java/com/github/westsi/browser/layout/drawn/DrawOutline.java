package com.github.westsi.browser.layout.drawn;

import com.github.westsi.browser.BrowserTab;

import java.awt.*;

public class DrawOutline implements IDrawCmd {

    private Integer top;
    private Integer left;
    private Integer bottom;
    private Integer right;
    private Color color;
    private String style;
    private Integer lineWidth;
    private boolean hasLeft, hasRight, hasTop, hasBottom;

    public DrawOutline(Integer x1, Integer y1, Integer x2, Integer y2, Color color, String style, Integer width,
                       boolean hasLeft, boolean hasRight, boolean hasTop, boolean hasBottom) {
        this.top = y1;
        this.left = x1;
        this.bottom = y2;
        this.right = x2;
        this.color = color;
        this.style = style;
        this.lineWidth = width;
        this.hasLeft = hasLeft;
        this.hasRight = hasRight;
        this.hasBottom = hasBottom;
        this.hasTop = hasTop;
    }
    @Override
    public Integer getTop() {
        return this.top;
    }

    @Override
    public Integer getBottom() {
        return this.bottom;
    }

    @Override
    public void execute(BrowserTab ctx, Graphics g) {
        // the way this is to be done is to draw rects for each side of the outline
        Color oldcolor = g.getColor();
//        g.setColor(this.color);
        g.setColor(Color.GREEN);
        // this section is for solid TODO: make it dependent on style
        if (this.hasTop) g.fillRect(this.left, this.top - ctx.getScrolled(), this.right-this.left, this.lineWidth); // top line
        if (this.hasLeft) g.fillRect(this.left, this.top - ctx.getScrolled(), this.lineWidth, this.bottom - this.top); // left side line
        if (this.hasBottom) g.fillRect(this.left, this.bottom - ctx.getScrolled(), this.right-this.left, this.lineWidth); // bottom line
        if (this.hasRight) g.fillRect(this.right, this.top-ctx.getScrolled(), this.lineWidth, this.bottom - this.top + 2); // right side line
        g.setColor(oldcolor);
    }
}
