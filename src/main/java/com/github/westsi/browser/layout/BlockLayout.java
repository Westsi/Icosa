package com.github.westsi.browser.layout;


import com.github.westsi.browser.Browser;
import com.github.westsi.browser.layout.drawn.DrawRect;
import com.github.westsi.browser.layout.drawn.DrawText;
import com.github.westsi.browser.layout.drawn.IDrawCmd;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.Quartet;
import com.github.westsi.browser.util.css.CSSUtils;
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
    private ArrayList<String> TEXTUAL_ELEMENTS = new ArrayList<>();

    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
    private Integer x, y, width, height, cursorX, cursorY, textIndent;
//    private float fontSize;
//    private String weight, style;
    private ArrayList<Quartet<Integer, String, Font, String>> line = new ArrayList<>();


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
        this.textIndent = 0;

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

        TEXTUAL_ELEMENTS.add("b");
        TEXTUAL_ELEMENTS.add("i");
        TEXTUAL_ELEMENTS.add("em");
        TEXTUAL_ELEMENTS.add("big");
        TEXTUAL_ELEMENTS.add("small");
    }

    @Override
    public void layout() {
        this.x = this.parent.getX();
//        System.out.println(((HTMLTag) this.parent.getNode()).getTag());
        if (Objects.equals(((HTMLTag) this.parent.getNode()).getTag(), "ul")) {
            System.out.println("LI INCREMENT");
            this.textIndent = 16;
        }
        this.width = this.parent.getWidth();
        if (this._previous != null) {
            this.y = this._previous.getY() + this._previous.getHeight();
        } else {
            this.y = this.parent.getY();
        }
        String mode = this.layoutMode();
        if (Objects.equals(mode, "block")) {
            ILayoutElement previous = null;
            this.anonymousBlockBoxChildren();
            for (HTMLElement child : this.node.getChildren()) {
                BlockLayout next = new BlockLayout(child, this, previous);
                this.children.add(next);
//                System.out.println(this.node.toString() + this.children);
                previous = next;
            }
        } else {
            this.cursorX = textIndent;
            this.cursorY = 0;
//            this.weight = "normal";
//            this.style = "roman";
//            this.fontSize = 12;

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
//        System.out.println("Layout for " + this.node.toString() + " completed successfully");
    }

    private void anonymousBlockBoxChildren() {
        /*
        * Anonymous block boxes. Sometimes, an element has a mix of text-like and container-like children.
        <div><i>Hello, </i><b>world!</b><p>So it began...</p></div>
        the <div> element has three children: the <i>, <b>, and <p> elements.
        * The first two are text-like; the last is container-like.
        * This is supposed to look like two paragraphs, one for the <i> and <b> and the second for the <p>. */

        /*
        * So essentially what we want to do here is loop through the children and put all sequential textual nodes
        * in a new <p> tag*/
        ArrayList<HTMLElement> newNodeChildren = new ArrayList<>();
        HTMLTag newP = new HTMLTag("p", this.node);
        for (HTMLElement child : this.node.getChildren()) {
            if (child instanceof HTMLTag && TEXTUAL_ELEMENTS.contains(((HTMLTag) child).getTag())) {
                // if this is the case, it meets the criteria to be added to the new <p>
                newP.addChild(child);
            } else if (child instanceof HTMLText) {
                // this also meets the criteria so is also added
                newP.addChild(child);
            } else {
                // the child is a tag that is not textual, so is not added to the paragraph
                // the paragraph must also be added beforehand
                newNodeChildren.add(newP);
                newP = new HTMLTag("p", this.node);
                newNodeChildren.add(child);
            }
        }
        // when the loop ends, add newP if it has any children
        if (!newP.getChildren().isEmpty()) {newNodeChildren.add(newP);}
        // then update the children
        this.node.setChildren(newNodeChildren);
    }

    private void recurse(HTMLElement tree) {
        if (tree instanceof HTMLText) {
            for (String word : ((HTMLText) tree).getText().split("\\s+")) {
                this.word(tree, word);
            }
        } else {
//            this.openTag(((HTMLTag) tree).getTag());
            if (Objects.equals(((HTMLTag) tree).getTag(), "br")) {
                this.flush();
            }
            for (HTMLElement child : tree.getChildren()) {
                this.recurse(child);
            }
//            this.closeTag(((HTMLTag) tree).getTag());
        }
    }

    private void word(HTMLElement node, String word) {
//        weight = node.style["font-weight"]
//        style = node.style["font-style"]
//        if style == "normal": style = "roman"
//        size = int(float(node.style["font-size"][:-2]) * .75)
//        font = get_font(size, weight, style)

//        color = node.style["color"]
//        self.line.append((self.cursor_x, word, font, color))
        String weight = node.getStyle().get("font-weight");
        String style = node.getStyle().get("font-style");
        String ssize = node.getStyle().get("font-size");
        //        if style == "normal": style = "roman"
        Integer size = (int) (Float.parseFloat(ssize.substring(0, ssize.length()-2)) * 0.75);
        if (Objects.equals(style, "normal")) {
            fontFlag = Font.PLAIN;
        } else if (Objects.equals(style, "italic")) {
            fontFlag = Font.ITALIC;
        }
        if (Objects.equals(weight, "bold")) {
            fontFlag |= Font.BOLD;
        }

//        Font font = Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize);
        Font font = Browser.fonts.get(fontFlag).deriveFont(fontFlag, size);
        Integer wordWidth = new Canvas().getFontMetrics(font).stringWidth(word);
        if (this.cursorX + wordWidth > this.width-HSTEP) this.flush();
        String color = node.getStyle().get("color");
        this.line.add(new Quartet<>(this.cursorX, word, font, color));
        this.cursorX += wordWidth + new Canvas().getFontMetrics(font).stringWidth(" ");
    }

//    private void openTag(String tag) {
//        switch (tag) {
//            case "b":
//                fontFlag |= Font.BOLD;
//                break;
//            case "em" :
//            case "i":
//                fontFlag |= Font.ITALIC;
////                System.out.println("MAKING ITALIC");
//                break;
//            case "small":
//                fontSize -= 2;
//                break;
//            case "big":
//                fontSize += 4;
//                break;
//            case "br":
//                this.flush();
//        }
//    }
//
//    private void closeTag(String tag) {
//        switch (tag) {
//            case "b":
//                fontFlag = fontFlag & ~Font.BOLD;
//                break;
//            case "em":
//            case "i":
//                fontFlag = fontFlag & ~Font.ITALIC;
//                break;
//            case "small":
//                fontSize += 2;
//                break;
//            case "big":
//                fontSize -= 4;
//                break;
//            case "p":
//                this.flush();
////                this.cursorY += (int) Math.round(VSTEP * 1.25);
//        }
//    }

    private void flush() {
        Integer maxAscent = 0;
        Integer maxDescent = 0;
        for (Quartet<Integer, String, Font, String> quartet : this.line) {
            int lma = new Canvas().getFontMetrics(quartet.getThird()).getMaxAscent();
            if (lma > maxAscent) {
                maxAscent = lma;
            }
            int lmd = new Canvas().getFontMetrics(quartet.getThird()).getMaxDescent();
            if (lmd > maxDescent) {
                maxDescent = lmd;
            }
        }
        Integer baseline = (int) (this.cursorY + 1.25 * maxAscent);

//        System.out.println(this.node.toString() + ( baseline + 1.25*maxDescent));


        for (Quartet<Integer, String, Font, String> quartet : this.line) {
            Integer ix = this.x + quartet.getFirst();
            Integer iy = this.y + baseline - new Canvas().getFontMetrics(quartet.getThird()).getMaxAscent();
            this.displayList.add(new Pair<>(new Point(ix, iy), new StyledString(quartet.getSecond(), quartet.getThird(), quartet.getFourth())));
        }

        this.cursorX = textIndent;
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
        String bgColor = this.node.getStyle().getOrDefault("background-color", "transparent");
        if (!this.node.getStyle().isEmpty()) {System.out.println("STYLE OF NODE" + this.node.getStyle());}
        if (!Objects.equals(bgColor, "transparent")) {
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            DrawRect rect = new DrawRect(this.x, this.y, x2, y2, CSSUtils.cssColorToJava(bgColor));
            cmds.add(rect);
        }

        if (Objects.equals(this.layoutMode(), "inline")) {
            for (Pair<Point, StyledString> pair: this.displayList) {
                DrawText text = new DrawText(pair.getFirst().x, pair.getFirst().y, pair.getSecond().string, pair.getSecond().font, CSSUtils.cssColorToJava(pair.getSecond().color));
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
