package com.github.westsi.browser.layout.drawn;

import com.github.westsi.browser.BrowserTab;

import java.awt.*;

public class DrawText implements IDrawCmd {

    private Integer top;
    private Integer left;
    private Integer bottom;
    private String text;
    private Font font;
    private Color color;
    public DrawText(Integer x1, Integer y1, String text, Font font, Color color) {
        this.top = y1;
        this.left = x1;
        this.text = text;
        this.font = font;
        this.color = color;

        this.bottom = y1 + new Canvas().getFontMetrics(font).getHeight();
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
        g.setFont(this.font);
        ctx.setFont(this.font);
        Color oldcolor = g.getColor();
        g.setColor(this.color);
        g.drawString(this.text, this.left, this.bottom - ctx.getScrolled());
        g.setColor(oldcolor);
        // this uses baseline and i think python original uses top, so might have to use bottom instead of top

    }
}
