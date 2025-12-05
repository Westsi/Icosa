package com.github.westsi.browser.layout.drawn;

import com.github.westsi.browser.BrowserTab;

import java.awt.*;

public class DrawRect implements IDrawCmd {

    private Integer top;
    private Integer left;
    private Integer bottom;
    private Integer right;
    private Color color;

    public DrawRect(Integer x1, Integer y1, Integer x2, Integer y2, Color color) {
        this.top = y1;
        this.left = x1;
        this.bottom = y2;
        this.right = x2;
        this.color = color;
    }
    @Override
    public Integer getTop() {
        return 0;
    }

    @Override
    public Integer getBottom() {
        return 0;
    }

    @Override
    public void execute(BrowserTab ctx, Graphics g) {
//        canvas.create_rectangle(
//                self.left, self.top - scroll,
//                self.right, self.bottom - scroll,
//                width=0,
//                fill=self.color)
        Color oldcolor = g.getColor();

//        System.out.println("Height of rect in drawRect call " + (this.bottom-this.top));
        g.setColor(this.color);
        g.fillRect(this.left, this.top - ctx.getScrolled(), this.right-this.left- BrowserTab.HSTEP, this.bottom - this.top);
        g.setColor(oldcolor);
    }
}
