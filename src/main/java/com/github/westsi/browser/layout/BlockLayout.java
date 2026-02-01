package com.github.westsi.browser.layout;


import com.github.westsi.browser.Browser;
import com.github.westsi.browser.BrowserTab;
import com.github.westsi.browser.layout.drawn.*;
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

//    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
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
        TEXTUAL_ELEMENTS.add("a");
    }

    @Override
    public void layout() {
        if (this.node instanceof HTMLTag) {
            System.out.println(((HTMLTag) this.node).getTag());
            if (((HTMLTag) this.node).getTag().equals("head")) {
//                System.out.println("returning head");
                return;
            }
        }
        this.x = this.parent.getX();
//        System.out.println(((HTMLTag) this.parent.getNode()).getTag());
        if (Objects.equals(((HTMLTag) this.parent.getNode()).getTag(), "ul")) {
//            System.out.println("incrementing x for ul");
            this.x += 16;
        }
        String maxWidth = this.node.getStyle().getOrDefault("max-width", "100000000px");
        Integer maxW = CSSUtils.resolveSize(maxWidth, this.node);
        Integer cssWidth = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("width", "0"), this.node);
        Integer top = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("top", "0"), this.node);
        Integer marginTop = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("margin-top", "0"), this.node);
        // TODO: these should be resolved into their respective margins based on write direction at some point
        // TODO: and then they also need to have cascading rules - idk where is best to do this
//        Integer marginBlockStart = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("margin-block-start", "0"), this.node);
//        Integer marginBlockEnd = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("margin-block-end", "0"), this.node);
//        Integer marginInlineStart = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("margin-inline-start", "0"), this.node);
//        Integer marginInlineEnd = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("margin-inline-end", "0"), this.node);
        String ml = this.node.getStyle().get("margin-left");
        String mr = this.node.getStyle().get("margin-right");
        Integer bottom = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("bottom", "0"), this.node);
        Integer marginBottom = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("margin-bottom", "0"), this.node);


//        if (top + marginTop != 0) System.out.println("top=" + top + ";margin-top=" + marginTop);

        // TODO: this seems to have broken resizing the window sometimes
        if (cssWidth == 0) {
            this.width = Math.min(this.parent.getWidth(), maxW);
        } else {
            this.width = cssWidth;
        }
        if (this._previous != null) {
            this.y = this._previous.getY() + this._previous.getHeight();
        } else {
            this.y = this.parent.getY();
        }

        this.y += top + marginTop;

        if (ml != null && mr != null) {
            if (ml.equals("auto") && mr.equals("auto")) {
                // center horizontally
                // x = left side
                Integer totalemptyspace = Browser.getInstance().WIDTH - this.width - this.x;
                if (totalemptyspace > 0) {
                    this.x += totalemptyspace / 2;
                }
            } else {
                Integer marginLeft = CSSUtils.resolveSize(ml, this.node);
                Integer marginRight = CSSUtils.resolveSize(mr, this.node);
                this.x += marginLeft;
                this.width -= marginRight; // TODO: is this the right way to do this?
            }
        }

        // TODO: idk if this works
        if (this.node.getStyle().getOrDefault("float", "").equals("right")) {
            System.out.println("floating, width = " + width);
            this.x = this.parent.getX() + this.parent.getWidth();
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
//            this.cursorX = textIndent;
//            this.cursorY = 0;
//            this.weight = "normal";
//            this.style = "roman";
//            this.fontSize = 12;

//            this.line = new ArrayList<>();
            this.newLine();
            this.recurse(this.node);
//            this.flush();
        }

        for (ILayoutElement child : this.children) {
            child.layout();
        }
        this.height = 0;
        for (ILayoutElement child : this.children) {
            this.height += child.getHeight();
        }

        this.height += bottom + marginBottom;

        if (this.node instanceof HTMLTag && ((HTMLTag) node).getTag().equals("nav")) {
            System.out.println("nav {" + this.node.getChildren() + "} dimensions: x="+x+";y="+y+";width="+width+";height="+height);
            System.out.println("\t\t" + this.node.getStyle());
        }


//        if (Objects.equals(mode, "block")) {
//            for (ILayoutElement child : this.children) {
//                this.height += child.getHeight();
//                System.out.println(this.node.toString() + this.height);
//            }
//        } else {
//            this.height = this.cursorY;
//        }
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
                if (!newP.getChildren().isEmpty()) newNodeChildren.add(newP);
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
                this.newLine();
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
        Integer size = (int) (CSSUtils.resolveSize(ssize, this.node) * 0.75);
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
        if (this.cursorX + wordWidth > this.width-HSTEP) this.newLine();
//        String color = node.getStyle().get("color");
//        this.line.add(new Quartet<>(this.cursorX, word, font, color));
//        this.cursorX += wordWidth + new Canvas().getFontMetrics(font).stringWidth(" ");
        ILayoutElement line = this.children.get(this.children.size() - 1);
        ILayoutElement prevword = (!line.getChildren().isEmpty()) ? line.getChildren().get(line.getChildren().size()-1) : null;
        TextLayout text = new TextLayout(node, word, line, prevword);
        line.getChildren().add(text);
        this.cursorX += wordWidth + new Canvas().getFontMetrics(font).stringWidth(" ");
    }

    private void newLine() {
        this.cursorX = 0;
        ILayoutElement lastLine = !this.children.isEmpty() ? this.children.get(this.children.size()-1) : null;
        LineLayout newline = new LineLayout(this.node, this, lastLine);
        this.children.add(newline);
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

//    private void flush() {
//        Integer maxAscent = 0;
//        Integer maxDescent = 0;
//        for (Quartet<Integer, String, Font, String> quartet : this.line) {
//            int lma = new Canvas().getFontMetrics(quartet.getThird()).getMaxAscent();
//            if (lma > maxAscent) {
//                maxAscent = lma;
//            }
//            int lmd = new Canvas().getFontMetrics(quartet.getThird()).getMaxDescent();
//            if (lmd > maxDescent) {
//                maxDescent = lmd;
//            }
//        }
//        Integer baseline = (int) (this.cursorY + 1.25 * maxAscent);
//
////        System.out.println(this.node.toString() + ( baseline + 1.25*maxDescent));
//
//
//        for (Quartet<Integer, String, Font, String> quartet : this.line) {
//            Integer ix = this.x + quartet.getFirst();
//            Integer iy = this.y + baseline - new Canvas().getFontMetrics(quartet.getThird()).getMaxAscent();
//            this.displayList.add(new Pair<>(new Point(ix, iy), new StyledString(quartet.getSecond(), quartet.getThird(), quartet.getFourth())));
//        }
//
//        this.cursorX = textIndent;
//        this.line = new ArrayList<>();
//        this.cursorY = (int) (baseline + 1.25 * maxDescent);
//
//    }

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
        if (this.node instanceof HTMLTag && Objects.equals(((HTMLTag) this.node).getTag(), "li")) {
//            System.out.println("adding circle for ul; " + this.node.getChildren() + this.node.getParent());
            Integer fs = CSSUtils.resolveSize(this.node.getStyle().get("font-size"), this.node);
            // todo: implement coloring based on font color
            DrawCircle dot = new DrawCircle(this.x-16, this.y+fs/4, this.x-16+fs/2, this.y+3*fs/4, Color.BLACK);
            cmds.add(dot);
        }
        String bgColor = this.node.getStyle().getOrDefault("background-color", "transparent");
        if (!Objects.equals(bgColor, "transparent")) {
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            DrawRect rect = new DrawRect(this.x, this.y, x2, y2, CSSUtils.cssColorToJava(bgColor));
            cmds.add(rect);
        }

        // TODO: a similar thing for border (remember this can be more complex to CSSParser and here - look in nav#toc border-bottom)
        String outlineWidth = this.node.getStyle().getOrDefault("outline-width", null);
        if (outlineWidth != null) {
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            Integer intoutlineWidth = CSSUtils.resolveSize(outlineWidth, this.node);
            String color = this.node.getStyle().getOrDefault("outline-color", null);
            String style = this.node.getStyle().getOrDefault("outline-style", null);
            if (color != null && style != null) {
                DrawOutline outlineCmd = new DrawOutline(this.x, this.y, x2, y2, CSSUtils.cssColorToJava(color), style,
                        intoutlineWidth, true, true, true, true);
                cmds.add(outlineCmd);
            }
        }

        Integer bottomBorderW = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("border-bottom-width", "0"), this.node);
        Integer leftBorderW = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("border-left-width", "0"), this.node);
        Integer rightBorderW = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("border-right-width", "0"), this.node);
        Integer topBorderW = CSSUtils.resolveSize(this.node.getStyle().getOrDefault("border-top-width", "0"), this.node);

        if (bottomBorderW != 0) {
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            String borderBottomColor = this.node.getStyle().getOrDefault("border-bottom-color", null);
            String borderBottomStyle = this.node.getStyle().getOrDefault("border-bottom-style", null);
            if (borderBottomColor != null && borderBottomStyle != null) {
                DrawOutline borderBottomCmd = new DrawOutline(this.x, this.y, x2, y2, CSSUtils.cssColorToJava(borderBottomColor), borderBottomStyle,
                        bottomBorderW, false, false, false, true);
                cmds.add(borderBottomCmd);
            }
        }

        if (leftBorderW != 0) {
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            String borderLeftColor = this.node.getStyle().getOrDefault("border-left-color", null);
            String borderLeftStyle = this.node.getStyle().getOrDefault("border-left-style", null);
            if (borderLeftColor != null && borderLeftStyle != null) {
                DrawOutline borderLeftCmd = new DrawOutline(this.x, this.y, x2, y2, CSSUtils.cssColorToJava(borderLeftColor), borderLeftStyle,
                        leftBorderW, true, false, false, false);
                cmds.add(borderLeftCmd);
            }
        }

        if (rightBorderW != 0) {
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            String borderRightColor = this.node.getStyle().getOrDefault("border-right-color", null);
            String borderRightStyle = this.node.getStyle().getOrDefault("border-right-style", null);
            if (borderRightColor != null && borderRightStyle != null) {
                DrawOutline borderRightCmd = new DrawOutline(this.x, this.y, x2, y2, CSSUtils.cssColorToJava(borderRightColor), borderRightStyle,
                        rightBorderW, true, false, false, false);
                cmds.add(borderRightCmd);
            }
        }

        if (topBorderW != 0) {
            Integer x2 = this.x + this.width;
            Integer y2 = this.y + this.height;
            String borderTopColor = this.node.getStyle().getOrDefault("border-top-color", null);
            String borderTopStyle = this.node.getStyle().getOrDefault("border-top-style", null);
            if (borderTopColor != null && borderTopStyle != null) {
                DrawOutline borderRightCmd = new DrawOutline(this.x, this.y, x2, y2, CSSUtils.cssColorToJava(borderTopColor), borderTopStyle,
                        topBorderW, true, false, false, false);
                cmds.add(borderRightCmd);
            }
        }

        if (Objects.equals(this.layoutMode(), "inline")) {
//            for (Pair<Point, StyledString> pair: this.displayList) {
//                DrawText text = new DrawText(pair.getFirst().x, pair.getFirst().y, pair.getSecond().string, pair.getSecond().font, CSSUtils.cssColorToJava(pair.getSecond().color));
//                cmds.add(text);
//            }
        }
        return cmds;
    }

//    @Override
//    public ArrayList<Pair<Point, StyledString>> getDisplayList() {
//        return this.displayList;
//    }
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
