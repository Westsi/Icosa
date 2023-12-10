package com.github.westsi.browser;

import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;
import com.github.westsi.browser.util.html.HTMLText;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.github.westsi.browser.BrowserTab.HSTEP;
import static com.github.westsi.browser.BrowserTab.VSTEP;

/**
 * Lays out a web page for a <code>BrowserTab</code>.
 * @author Westsi
 * @version %I%
 */
public class Layout {
    private Integer prevWidth = HSTEP;
    private final StringBuilder buf = new StringBuilder();
    private final Float defaultFontSize;
    private Float fontSize;
    private Integer width;
    private Integer height;

    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();

    public ArrayList<Pair<Point, StyledString>> getDisplayList() {
        return displayList;
    }

    public Layout(Float defaultFontSize, Integer width, Integer height) {
        this.defaultFontSize = defaultFontSize;
        this.fontSize = defaultFontSize;
        this.width = width;
        this.height = height;
    }

    /**
     * Lays out tokens on page, with handling for some <code>HTMLTag</code>s and <code>HTMLText</code>.
     * @param tokens The lexed <code>HTMLElement</code>s that need to be lain out.
     */
    public void LayoutWebPage(ArrayList<HTMLElement> tokens) {
        ReInitVariables();
        Integer cursorY = VSTEP;
        ArrayList<Pair<Point, StyledString>> dl = new ArrayList<>();
        int fontFlag = Font.PLAIN;
        for (HTMLElement el : tokens) {
            if (el instanceof HTMLText) {
                HTMLText htmlText = (HTMLText) el;
                String text = htmlText.getText();
                for (int i=0,n=text.length(); i<n; i+= Character.charCount(text.codePointAt(i))) {
                    char ch = (char) text.codePointAt(i);
                    if (ch != '\n') {
                        buf.append(ch);
                        int swidth = new Canvas().getFontMetrics(Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize)).stringWidth(buf.toString()) + prevWidth;

                        if (swidth >= this.width - HSTEP * 2) {
                            dl.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize))));
                            buf.setLength(0);
                            prevWidth = HSTEP;
                            cursorY += (int) Math.round(VSTEP * 1.25);

                        }
                    }
                }
            } else {
                HTMLTag tag = (HTMLTag) el;
                if (tag.getTag().equals("br")) {
                    if (!buf.toString().isEmpty()) dl.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize))));
                    buf.setLength(0);
                    prevWidth = HSTEP;
                    cursorY += VSTEP * 2;

                } else {
                    if (!buf.toString().isEmpty()) {
                        dl.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize))));
                        prevWidth += (new Canvas().getFontMetrics(Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize)).stringWidth(buf.toString()));
                    }
                    buf.setLength(0);
                    switch (tag.getTag()) {
                        case "b":
                            fontFlag |= Font.BOLD;
                            break;
                        case "i":
                            fontFlag |= Font.ITALIC;
                            break;
                        case "/b":
                            fontFlag = fontFlag & ~Font.BOLD;
                            break;
                        case "/i":
                            fontFlag = fontFlag & ~Font.ITALIC;
                            break;
                        case "br":
                            break;
                        case "small":
                            fontSize -= 2;
                            break;
                        case "big":
                            fontSize += 4;
                            break;
                        case "/big":
                            fontSize -= 4;
                            break;
                        case "/small":
                            fontSize += 2;
                            break;
                    }
                }
            }
        }
        if (!buf.toString().isEmpty()) dl.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, fontSize))));
//        System.out.println(dl);
        this.displayList = dl;
    }

    private void ReInitVariables() {
        this.fontSize = defaultFontSize;
        this.buf.setLength(0);
        this.prevWidth = HSTEP;
    }

    public void updateResize(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }
}
