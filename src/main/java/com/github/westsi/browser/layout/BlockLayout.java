package com.github.westsi.browser.layout;


import com.github.westsi.browser.Browser;
import com.github.westsi.browser.layout.drawn.DrawRect;
import com.github.westsi.browser.layout.drawn.DrawText;
import com.github.westsi.browser.layout.drawn.IDrawCmd;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.Triplet;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;
import com.github.westsi.browser.util.html.HTMLText;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.westsi.browser.BrowserTab.HSTEP;
import static com.github.westsi.browser.BrowserTab.VSTEP;

public class BlockLayout implements ILayoutElement{
    private HTMLElement node;
    private ILayoutElement parent;
    private ILayoutElement _previous;
    private ArrayList<ILayoutElement> children;
    private ArrayList<String> BLOCK_ELEMENTS = new ArrayList<>();

    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
    private Integer x, y, width, height, cursorX, cursorY;
    private float fontSize;
    private String weight, style;
    private ArrayList<Triplet<Integer, String, Font>> line = new ArrayList<>();

    int fontFlag = Font.PLAIN;

    public BlockLayout(HTMLElement node, ILayoutElement parent, ILayoutElement previous) {
        this.node = node;
        this.parent = parent;
        this.children = new ArrayList<>();
        this._previous = previous;
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.cursorX = HSTEP;
        this.cursorY = VSTEP;

        BLOCK_ELEMENTS.add("html");
        BLOCK_ELEMENTS.add("body");
        BLOCK_ELEMENTS.add("article");
        BLOCK_ELEMENTS.add("section");
        BLOCK_ELEMENTS.add("nav");
        BLOCK_ELEMENTS.add("aside");
        BLOCK_ELEMENTS.add("h1");
        BLOCK_ELEMENTS.add("h2");
        BLOCK_ELEMENTS.add("h3");
        BLOCK_ELEMENTS.add("h4");
        BLOCK_ELEMENTS.add("h5");
        BLOCK_ELEMENTS.add("h6");
        BLOCK_ELEMENTS.add("hgroup");
        BLOCK_ELEMENTS.add("header");
        BLOCK_ELEMENTS.add("footer");
        BLOCK_ELEMENTS.add("address");
        BLOCK_ELEMENTS.add("p");
        BLOCK_ELEMENTS.add("hr");
        BLOCK_ELEMENTS.add("pre");
        BLOCK_ELEMENTS.add("blockquote");
        BLOCK_ELEMENTS.add("ol");
        BLOCK_ELEMENTS.add("ul");
        BLOCK_ELEMENTS.add("menu");
        BLOCK_ELEMENTS.add("li");
        BLOCK_ELEMENTS.add("dl");
        BLOCK_ELEMENTS.add("dt");
        BLOCK_ELEMENTS.add("dd");
        BLOCK_ELEMENTS.add("figure");
        BLOCK_ELEMENTS.add("figcaption");
        BLOCK_ELEMENTS.add("main");
        BLOCK_ELEMENTS.add("div");
        BLOCK_ELEMENTS.add("table");
        BLOCK_ELEMENTS.add("form");
        BLOCK_ELEMENTS.add("fieldset");
        BLOCK_ELEMENTS.add("legend");
        BLOCK_ELEMENTS.add("details");
        BLOCK_ELEMENTS.add("summary");
    }

    @Override
    public void layout() {
        this.x = this.parent.getX();
        this.width = this.parent.getWidth();
        if (this._previous != null) {
            this.y = this._previous.getY() + this._previous.getHeight();
        } else {
            this.y = this.parent.getY();
        }
        String mode = this.layoutMode();
        if (Objects.equals(mode, "block")) {
            ILayoutElement previous = null;
            for (HTMLElement child : this.node.getChildren()) {
                BlockLayout next = new BlockLayout(child, this, previous);
                this.children.add(next);
                System.out.println(this.node.toString() + this.children);
                previous = next;
            }
        } else {
            this.cursorX = 0;
            this.cursorY = 0;
            this.weight = "normal";
            this.style = "roman";
            this.fontSize = 12;

            this.line = new ArrayList<>();
            this.recurse(this.node);
            this.flush();
        }

        for (ILayoutElement child : this.children) {
            child.layout();
        }

        if (Objects.equals(mode, "block")) {
            for (ILayoutElement child : this.children) {
                this.height += child.getHeight();
                System.out.println(this.node.toString() + this.height);
            }
        } else {
            this.height = this.cursorY;
        }
    }

    private void recurse(HTMLElement tree) {
        if (tree instanceof HTMLText) {
            for (String word : ((HTMLText) tree).getText().split(" ")) {
                this.word(word);
            }
        } else {
            this.openTag(((HTMLTag) tree).getTag());
            for (HTMLElement child : tree.getChildren()) {
                this.recurse(child);
            }
            this.closeTag(((HTMLTag) tree).getTag());
        }
    }

    private void word(String word) {
        Font font = Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize);
        Integer wordWidth = new Canvas().getFontMetrics(font).stringWidth(word);
        if (this.cursorX + wordWidth > this.width) this.flush();
        this.line.add(new Triplet<>(this.cursorX, word, font));
        this.cursorX += wordWidth + new Canvas().getFontMetrics(font).stringWidth(" ");
    }

    private void openTag(String tag) {
        switch (tag) {
            case "b":
                fontFlag |= Font.BOLD;
                break;
            case "i":
                fontFlag |= Font.ITALIC;
                break;
            case "small":
                fontSize -= 2;
                break;
            case "big":
                fontSize += 4;
                break;
            case "br":
                this.flush();
        }
    }

    private void closeTag(String tag) {
        switch (tag) {
            case "b":
                fontFlag = fontFlag & ~Font.BOLD;
                break;
            case "i":
                fontFlag = fontFlag & ~Font.ITALIC;
                break;
            case "small":
                fontSize += 2;
                break;
            case "big":
                fontSize -= 4;
                break;
            case "p":
                this.flush();
                this.cursorY += (int) Math.round(VSTEP * 1.25);
        }
    }

    private void flush() {
        Integer maxAscent = 0;
        Integer maxDescent = 0;
        for (Triplet<Integer, String, Font> triplet : this.line) {
            int lma = new Canvas().getFontMetrics(triplet.getThird()).getMaxAscent();
            if (lma > maxAscent) {
                maxAscent = lma;
            }
            int lmd = new Canvas().getFontMetrics(triplet.getThird()).getMaxDescent();
            if (lmd > maxDescent) {
                maxDescent = lmd;
            }
        }
        Integer baseline = (int) (this.cursorY + 1.25 * maxAscent);

        System.out.println(this.node.toString() + ( baseline + 1.25*maxDescent));


        for (Triplet<Integer, String, Font> triplet : this.line) {
            Integer ix = this.x + triplet.getFirst();
            Integer iy = this.y + baseline - new Canvas().getFontMetrics(triplet.getThird()).getMaxAscent();
            this.displayList.add(new Pair<>(new Point(ix, iy), new StyledString(triplet.getSecond(), triplet.getThird())));
        }

        this.cursorX = 0;
        this.line = new ArrayList<>();
        this.cursorY = (int) (baseline + 1.25 * maxDescent);

    }

    public String layoutMode() {
        if (this.node instanceof HTMLText) {
            return "inline";
        } else {
            for (HTMLElement child : this.node.getChildren()) {
                if (child instanceof HTMLTag) {
                    if (BLOCK_ELEMENTS.contains(((HTMLTag) child).getTag())) {return "block";}
                }
            }
            if (this.node.getChildren() != null) {return "inline";}
        }
        return "block";
    }

    public ArrayList<IDrawCmd> paint() {
        ArrayList<IDrawCmd> cmds = new ArrayList<>();
        if (this.node instanceof HTMLTag && Objects.equals(((HTMLTag) this.node).getTag(), "pre")) {
            System.out.println("CALLING PRE TAG THING for " + this.node.toString());
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            DrawRect rect = new DrawRect(this.x, this.y, x2, y2, new Color(255, 0, 0));
            cmds.add(rect);
        }

        if (Objects.equals(this.layoutMode(), "inline")) {
            for (Pair<Point, StyledString> pair: this.displayList) {
                DrawText text = new DrawText(pair.getFirst().x, pair.getFirst().y, pair.getSecond().string, pair.getSecond().font);
                cmds.add(text);
            }
        }
        return cmds;
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

}
