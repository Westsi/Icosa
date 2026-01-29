package com.github.westsi.browser.layout.drawn;

import com.github.westsi.browser.BrowserTab;

import java.awt.*;

public class DrawCircle implements IDrawCmd {

    private Integer top;
    private Integer left;
    private Integer bottom;
    private Integer right;
    private Color color;

    public DrawCircle(Integer x1, Integer y1, Integer x2, Integer y2, Color color) {
        this.top = y1;
        this.left = x1;
        this.bottom = y2;
        this.right = x2;
        this.color = color;
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
        Color oldcolor = g.getColor();
        g.setColor(this.color);
        g.fillOval(this.left, this.top - ctx.getScrolled(), this.right-this.left- BrowserTab.HSTEP, this.bottom - this.top);
        g.setColor(oldcolor);
    }
}
