package com.github.westsi.browser;

import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.Triplet;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;
import com.github.westsi.browser.util.html.HTMLText;

import javax.swing.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.github.westsi.browser.util.html.HTMLParser.lex;

public class BrowserTab extends JPanel {
    private Integer HSTEP = new Canvas().getFontMetrics(Browser.fonts.get(Font.PLAIN)).charWidth('m');
    private Integer VSTEP = new Canvas().getFontMetrics(Browser.fonts.get(Font.PLAIN)).getHeight();
    private Integer refreshRate;

    private Integer scrolled = 0;

    private URL url;

    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
    private ArrayList<HTMLElement> tokens = new ArrayList<>();

    private Integer width;
    private Integer height;

    public BrowserTab(String url, Integer refreshRate, Integer width, Integer height) {
        this.refreshRate = refreshRate;
        this.url = new URL(url);
        this.height = height;
        this.width = width;
        this.setFocusable(true);

        this.addMouseWheelListener(e -> {
            String message;
            int notches = e.getWheelRotation();
            if (notches < 0) {
                message = "Mouse wheel moved UP "
                        + -notches + " notch(es)" + "\n";
            } else {
                message = "Mouse wheel moved DOWN "
                        + notches + " notch(es)" + "\n";
            }
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                message += "Scroll type: WHEEL_UNIT_SCROLL\n";
                scrolled += VSTEP * e.getUnitsToScroll();
            } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
                message += "Scroll type: WHEEL_BLOCK_SCROLL\n";
            }
            scrolled = Math.max(0, scrolled);
            System.out.println(message + scrolled);
        });
    }

    public void updateResize() {
        System.out.println("updating due to resize");
        this.width = Browser.WIDTH - 50; // frame boundaries + extra crap
        this.height = Browser.HEIGHT - 100; // frame boundaries + extra crap
        this.LayoutWebPage();
    }

    @Override
    public Graphics getGraphics() {
        return super.getGraphics();
    }

    @Override
    public String getName() {
        return this.url.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
//        System.out.println("rerendering tab " + url);
        super.paintComponent(g);
        for (Pair<Point, StyledString> renderchunk : displayList) {
            if (renderchunk.getFirst().y > scrolled + height) continue;
            if (renderchunk.getFirst().y + VSTEP < scrolled) continue;
            g.setFont(renderchunk.getSecond().font);
            this.setFont(renderchunk.getSecond().font);
            g.drawString(renderchunk.getSecond().string, renderchunk.getFirst().x, renderchunk.getFirst().y - scrolled);
        }
    }



    public void LoadWebPage() {
        String body;
        try {
            body = this.url.Request();
        } catch (URISyntaxException e) {
            body = "<html><body><h1>Incorrect URI.</h1></body></html>";
        }  catch (InterruptedException e) {
            body = "<html><body><h1>Page load was interrupted.</h1></body></html>";
        } catch (Exception e) {
            body = "<html><body><h1>Something went wrong.</h1></body></html>";
        }
        this.tokens = lex(body);
        for (HTMLElement e : tokens) {
            System.out.println(e);
        }
        this.LayoutWebPage();
    }

//    public ArrayList<Triplet<Integer, Integer, String>> OldLayoutWebPage() {
//        ArrayList<Triplet<Integer, Integer, String>> displayList = new ArrayList<>();
//        Integer cursorY = VSTEP*3;
//        StringBuilder tempStr = new StringBuilder();
//        for (int i=0,n=text.length(); i<n; i+= Character.charCount(text.codePointAt(i))) {
//            char ch = (char) text.codePointAt(i);
//            if (ch == '\n') {
//                displayList.add(new Triplet<>(HSTEP, cursorY, tempStr.toString()));
//                tempStr.setLength(0);
//                cursorY += VSTEP * 2;
//            }
//            else {
//                tempStr.append(ch);
//                Integer width = new Canvas().getFontMetrics(Browser.font).stringWidth(tempStr.toString());
//
//                if (width >= this.width - HSTEP) {
//                    displayList.add(new Triplet<>(HSTEP, cursorY, tempStr.toString()));
//                    tempStr.setLength(0);
//                    cursorY += (int) Math.round(VSTEP * 1.25);
//                }
//            }
//        }
//        displayList.add(new Triplet<>(HSTEP, cursorY, tempStr.toString()));
//
//        return displayList;
//    }

    public void LayoutWebPage() {
        ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
        Font curFont = Browser.fonts.get(Font.PLAIN).deriveFont(Font.PLAIN, 12.0f);
        Integer cursorY = VSTEP * 5;
        Integer prevWidth = HSTEP;
        StringBuilder tempStr = new StringBuilder();
        for (HTMLElement el : this.tokens) {
            if (el instanceof HTMLText) {
                HTMLText htmlText = (HTMLText) el;
                String text = htmlText.getText();
                for (int i=0,n=text.length(); i<n; i+= Character.charCount(text.codePointAt(i))) {
                    char ch = (char) text.codePointAt(i);
                    if (ch == '\n') {
                    } else {
                        tempStr.append(ch);
                        Integer width = new Canvas().getFontMetrics(curFont).stringWidth(tempStr.toString());

                        if (width >= this.width - HSTEP) {
                            displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(tempStr.toString(), curFont)));
                            tempStr.setLength(0);
                            prevWidth = HSTEP;
                            cursorY += (int) Math.round(VSTEP * 1.25);
                        }
                    }
                }
            } else {
                HTMLTag tag = (HTMLTag) el;
                switch (tag.getTag()) {
                    case "b":
                        prevWidth += (new Canvas().getFontMetrics(curFont).stringWidth(tempStr.toString()));
                        System.out.println("b tag" + prevWidth);
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(tempStr.toString(), curFont)));
                        tempStr.setLength(0);
                        curFont = Browser.fonts.get(Font.BOLD).deriveFont(Font.BOLD, 12.0f);
                        break;
                    case "i":
                        prevWidth += (new Canvas().getFontMetrics(curFont).stringWidth(tempStr.toString()));
                        System.out.println("i tag" + prevWidth);
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(tempStr.toString(), curFont)));
                        tempStr.setLength(0);
                        curFont = Browser.fonts.get(Font.ITALIC).deriveFont(Font.ITALIC, 12.0f);
                        break;
                    case "/b", "/i":
                        prevWidth += (new Canvas().getFontMetrics(curFont).stringWidth(tempStr.toString()));
                        System.out.println("leaving tag" + prevWidth);
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(tempStr.toString(), curFont)));
                        tempStr.setLength(0);
                        curFont = Browser.fonts.get(Font.PLAIN).deriveFont(12.0f);
                        break;
                    case "br":
                        displayList.add(new Pair<>(new Point(prevWidth, cursorY), new StyledString(tempStr.toString(), curFont)));
                        tempStr.setLength(0);
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
