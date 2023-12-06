package com.github.westsi.browser;

import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;
import com.github.westsi.browser.util.html.HTMLText;

import java.awt.*;
import java.util.ArrayList;

import static com.github.westsi.browser.BrowserTab.HSTEP;
import static com.github.westsi.browser.BrowserTab.VSTEP;

public class Layout {
    private Integer cursorY = VSTEP * 5;
    private Integer prevWidth = HSTEP;
    private StringBuilder buf = new StringBuilder();
    private Float defaultFontSize;
    private Float fontSize;
    private int fontFlag = Font.PLAIN;
    private Integer width;
    private Integer height;

    public ArrayList<Pair<Point, StyledString>> getDisplayList() {
        return displayList;
    }

    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();

    public Layout(Float defaultFontSize, Integer width, Integer height) {
        this.defaultFontSize = defaultFontSize;
        this.fontSize = defaultFontSize;
        this.width = width;
        this.height = height;
    }

    public void LayoutWebPage(ArrayList<HTMLElement> tokens) {
        ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
        int fontFlag = Font.PLAIN;
        for (HTMLElement el : tokens) {
            if (el instanceof HTMLText) {
                HTMLText htmlText = (HTMLText) el;
                String text = htmlText.getText();
                for (int i=0,n=text.length(); i<n; i+= Character.charCount(text.codePointAt(i))) {
                    char ch = (char) text.codePointAt(i);
                    if (ch == '\n') {
                    } else {
                        buf.append(ch);
                        Integer width = new Canvas().getFontMetrics(Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f)).stringWidth(buf.toString());

                        if (width >= this.width - HSTEP) {
                            displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f))));
                            buf.setLength(0);
                            prevWidth = HSTEP;
                            cursorY += (int) Math.round(VSTEP * 1.25);
                        }
                    }
                }
            } else {
                HTMLTag tag = (HTMLTag) el;
                switch (tag.getTag()) {
                    case "b":
                        prevWidth += (new Canvas().getFontMetrics(Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f)).stringWidth(buf.toString()));
                        System.out.println("b tag" + prevWidth);
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f))));
                        buf.setLength(0);
                        fontFlag |= Font.BOLD;
                        break;
                    case "i":
                        prevWidth += (new Canvas().getFontMetrics(Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f)).stringWidth(buf.toString()));
                        System.out.println("i tag" + prevWidth);
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f))));
                        buf.setLength(0);
                        fontFlag |= Font.ITALIC;
                        break;
                    case "/b":
                        prevWidth += (new Canvas().getFontMetrics(Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f)).stringWidth(buf.toString()));
                        System.out.println("leaving tag" + prevWidth);
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f))));
                        buf.setLength(0);
                        fontFlag = fontFlag & ~Font.BOLD;
                        break;
                    case "/i":
                        prevWidth += (new Canvas().getFontMetrics(Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f)).stringWidth(buf.toString()));
                        System.out.println("leaving tag" + prevWidth);
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f))));
                        buf.setLength(0);
                        fontFlag = fontFlag & ~Font.ITALIC;
                        break;
                    case "br":
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(buf.toString(), Browser.fonts.get(fontFlag).deriveFont(fontFlag, 12.0f))));
                        buf.setLength(0);
                        prevWidth = HSTEP;
                        cursorY += VSTEP * 2;
                        break;
                };
            }
        }
        System.out.println(displayList);
        this.displayList = displayList;
    }
}
