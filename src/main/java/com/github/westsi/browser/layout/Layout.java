package com.github.westsi.browser.layout;

import com.github.westsi.browser.Browser;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;
import com.github.westsi.browser.util.html.HTMLText;

import java.awt.*;
import java.util.ArrayList;

import static com.github.westsi.browser.BrowserTab.HSTEP;
import static com.github.westsi.browser.BrowserTab.VSTEP;

/**
 * Lays out a web page for a <code>BrowserTab</code>.
 * @author Westsi
 * @version %I%
 */
public class Layout {
    int cursorX = HSTEP;
    int cursorY = VSTEP;

    public ArrayList<Pair<Point, StyledString>> getDisplayList() {
        return displayList;
    }

    ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
    int fontFlag = Font.PLAIN;
    private final Float defaultFontSize;
    private Float fontSize;
    private Integer width;
    private Integer height;

    public Layout(Float defaultFontSize, Integer width, Integer height) {
        this.defaultFontSize = defaultFontSize;
        this.fontSize = defaultFontSize;
        this.width = width;
        this.height = height;
    }

    public void updateResize(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    public void resetVariables() {
        this.fontSize = defaultFontSize;
        this.displayList.clear();
        this.cursorY = VSTEP;
        this.cursorX = HSTEP;
    }

    /**
     * Lays out tokens on page, with handling for some <code>HTMLTag</code>s and <code>HTMLText</code>.
     * @param root The root of the <code>HTMLElement</code> tree that needs to be laid out.
     */
    public void layout(HTMLElement root) {
        this.resetVariables();
        recurse(root);
        this.flush();
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
        if (this.cursorX + wordWidth > this.width - HSTEP) this.flush();
        EventQueue.invokeLater(new Runnable() {
            int cx = cursorX;
            int cy = cursorY;
            String w = word;
            Font f = font;
            @Override
            public void run() {
                displayList.add(new Pair<>(new Point(cx, cy), new StyledString(w, f)));
            }
        });
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
        this.cursorY += (int) Math.round(VSTEP * 1.25);
        this.cursorX = HSTEP;
    }
}
