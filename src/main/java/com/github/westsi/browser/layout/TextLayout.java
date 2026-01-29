package com.github.westsi.browser.layout;

import com.github.westsi.browser.Browser;
import com.github.westsi.browser.layout.drawn.DrawRect;
import com.github.westsi.browser.layout.drawn.DrawText;
import com.github.westsi.browser.layout.drawn.IDrawCmd;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.css.CSSUtils;
import com.github.westsi.browser.util.html.HTMLElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class TextLayout implements ILayoutElement {
    private HTMLElement node;
    private ILayoutElement parent;
    private ILayoutElement _previous;
    private String word;
    private ArrayList<ILayoutElement> children;
    private int fontFlag = Font.PLAIN;
    private Integer width, height, x, y;
    private Font font;
    private boolean underlined;
    public TextLayout(HTMLElement node, String word, ILayoutElement parent, ILayoutElement previous) {
        this.node = node;
        this.parent = parent;
        this._previous = previous;
        this.children = new ArrayList<>();
        this.word = word;
    }

    @Override
    public HTMLElement getNode() {
        return this.node;
    }

    public Font getFont() {
        return this.font;
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

    public void setY(Integer ny) {
        this.y = ny;
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
        String weight = node.getStyle().get("font-weight");
        String style = node.getStyle().get("font-style");
        String ssize = node.getStyle().get("font-size");
        String textDeco = node.getStyle().get("text-decoration");
        if (textDeco != null) {
//            System.out.println(node.getStyle());
        }
        Integer size = (int) (CSSUtils.resolveSize(ssize, node) * 0.75);
//        if (size != 12) System.out.println("font size " + size);
        underlined = Objects.equals(textDeco, "underline");
        if (Objects.equals(style, "normal")) {
            fontFlag = Font.PLAIN;
        } else if (Objects.equals(style, "italic")) {
            fontFlag = Font.ITALIC;
        }
        if (Objects.equals(weight, "bold")) {
            fontFlag |= Font.BOLD;
        }

//        if (fontFlag != Font.PLAIN) {
//            System.out.println(fontFlag);
//            System.out.println(node.getStyle());
//            System.out.println(node);
//        }

        this.font = Browser.fonts.get(fontFlag).deriveFont(fontFlag, size);
        this.width = new Canvas().getFontMetrics(this.font).stringWidth(word);

        if (this._previous != null) {
            Integer space = new Canvas().getFontMetrics(this.font).stringWidth(" ");
            this.x = this._previous.getX() + space + this._previous.getWidth();
        } else {
            this.x = this.parent.getX();
        }
        this.height = new Canvas().getFontMetrics(this.font).getHeight();
    }

    @Override
    public ArrayList<IDrawCmd> paint() {
        ArrayList<IDrawCmd> a = new ArrayList<>();
        a.add(new DrawText(this.x, this.y, this.word, this.font, CSSUtils.cssColorToJava(node.getStyle().get("color")), underlined));
        return a;
    }

}
