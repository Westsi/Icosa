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
    public static Integer HSTEP = new Canvas().getFontMetrics(Browser.fonts.get(Font.PLAIN)).charWidth('m');
    public static Integer VSTEP = new Canvas().getFontMetrics(Browser.fonts.get(Font.PLAIN)).getHeight();
    private Integer refreshRate;

    private Integer scrolled = 0;

    private URL url;

//    private ArrayList<Pair<Point, StyledString>> displayList = new ArrayList<>();
    private Layout layout;
    private ArrayList<HTMLElement> tokens = new ArrayList<>();

    private Integer width;
    private Integer height;

    public BrowserTab(String url, Integer refreshRate, Integer width, Integer height) {
        this.refreshRate = refreshRate;
        this.url = new URL(url);
        this.height = height;
        this.width = width;
        this.layout = new Layout(12.0f, width, height);
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
        this.layout.LayoutWebPage(this.tokens);
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
        for (Pair<Point, StyledString> renderchunk : this.layout.getDisplayList()) {
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
        this.layout.LayoutWebPage(this.tokens);
    }
}
