package com.github.westsi.browser;

import com.github.westsi.browser.util.Triplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.github.westsi.browser.util.HTMLParser.lex;
import static java.awt.BorderLayout.EAST;

public class BrowserTab extends JPanel {
    boolean thing = false;

    private final Integer HSTEP = 13;
    private final Integer VSTEP = 18;
    private Integer refreshRate;

    private Integer scrolled = 0;

    private URL url;

    private ArrayList<Triplet<Integer, Integer, String>> displayList = new ArrayList<>();
    private String text = "Loading...";

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
        this.width = Browser.WIDTH - 100; // frame boundaries + extra crap
        this.height = Browser.HEIGHT - 100; // frame boundaries + extra crap
        this.displayList = this.LayoutWebPage(this.text);
    }

    @Override
    public String getName() {
        return this.url.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
//        System.out.println("rerendering tab " + url);
        super.paintComponent(g);
        for (Triplet<Integer, Integer, String> renderchunk : displayList) {
            if (renderchunk.getSecond() > scrolled + height) continue;
            if (renderchunk.getSecond() + VSTEP < scrolled) continue;
            g.drawString(renderchunk.getThird(), renderchunk.getFirst(), renderchunk.getSecond() - scrolled);
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
        this.text = lex(body);
        this.displayList = this.LayoutWebPage(text);
    }

    public ArrayList<Triplet<Integer, Integer, String>> LayoutWebPage(String text) {
        ArrayList<Triplet<Integer, Integer, String>> displayList = new ArrayList<>();
        Integer cursorX = HSTEP;
        Integer cursorY = VSTEP;
        for (int i=0,n=text.length(); i<n; i+= Character.charCount(text.codePointAt(i))) {
            char ch = (char) text.codePointAt(i);
            if (ch == '\n') {
                cursorX = HSTEP;
                cursorY += VSTEP * 2;
            }
            else {
                displayList.add(new Triplet<>(cursorX, cursorY, ((Character) ch).toString()));
                cursorX += HSTEP;
                if (cursorX >= this.width - HSTEP) {
                    cursorY += VSTEP;
                    cursorX = HSTEP;
                }
            }
        }

        return displayList;
    }
}
