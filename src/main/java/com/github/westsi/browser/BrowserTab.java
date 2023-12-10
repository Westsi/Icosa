package com.github.westsi.browser;

import com.github.westsi.browser.layout.OldLayout;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLParser;
import com.github.westsi.browser.util.url.URL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;

/**
 * Handles layout, rendering, and interactivity for an individual browser tab.
 * Supports scrolling.
 * @author Westsi
 * @version %I%
 * @see OldLayout
 */
public class BrowserTab extends JPanel {
    public static Integer HSTEP = new Canvas().getFontMetrics(Browser.fonts.get(Font.PLAIN)).charWidth('m');
    public static Integer VSTEP = new Canvas().getFontMetrics(Browser.fonts.get(Font.PLAIN)).getHeight() * 5;

    private Integer scrolled = 0;
    private final URL url;
    private final OldLayout layout;
    private HTMLElement root;
    private Integer width;
    private Integer height;

    public BrowserTab(String url, Integer width, Integer height) {
        this.url = new URL(url);
        this.height = height - 100;
        this.width = width - 50;
        this.layout = new OldLayout(12.0f, this.width, this.height);
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
        this.layout.updateResize(this.width, this.height);
        this.layout.layout(this.root);
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
            if (renderchunk.getFirst().y < scrolled) continue;
            g.setFont(renderchunk.getSecond().font);
            this.setFont(renderchunk.getSecond().font);
            g.drawString(renderchunk.getSecond().string, renderchunk.getFirst().x, renderchunk.getFirst().y - scrolled);
        }
    }



    public void LoadWebPage() {
        System.out.println("loading " + url);
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
        HTMLParser parser = new HTMLParser();
        HTMLElement root = parser.parse(body);
        this.root = root;
        parser.printTree(root, 0);
        this.layout.layout(this.root);
    }
}
