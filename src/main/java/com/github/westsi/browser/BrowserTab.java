package com.github.westsi.browser;

import com.github.westsi.browser.layout.DocumentLayout;
import com.github.westsi.browser.layout.ILayoutElement;
import com.github.westsi.browser.layout.OldLayout;
import com.github.westsi.browser.layout.TextLayout;
import com.github.westsi.browser.layout.drawn.IDrawCmd;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.css.CSSParser;
import com.github.westsi.browser.util.css.CSSUtils;
import com.github.westsi.browser.util.css.ISelector;
import com.github.westsi.browser.util.css.SortByPriority;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLParser;
import com.github.westsi.browser.util.html.HTMLTag;
import com.github.westsi.browser.util.html.HTMLText;
import com.github.westsi.browser.util.url.URL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
    private URL url;
//    private final OldLayout layout;
    private HTMLElement root;
    private Integer width;
    private Integer height;
    private ArrayList<IDrawCmd> displayList;
    private DocumentLayout document;
    private ArrayList<Pair<URL, Integer>> pastUrls = new ArrayList<>();
    private Browser parent;

    public BrowserTab(String url, Integer width, Integer height, Browser parent) {
        this.url = new URL(url);
        this.height = height - 100;
        this.width = width - 50;
        this.parent = parent;
//        this.layout = new OldLayout(12.0f, this.width, this.height);
        this.displayList = new ArrayList<>();
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

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                click(e);
            }
        });
    }

    public void updateResize() {
        System.out.println("updating due to resize");
        this.width = Browser.WIDTH - 50; // frame boundaries + extra crap
        this.height = Browser.HEIGHT - 100; // frame boundaries + extra crap
//        this.layout.updateResize(this.width, this.height);
//        this.layout.layout(this.root);
    }

    private void click(MouseEvent e) {
        Integer cx = e.getX();
        Integer cy = e.getY() + this.scrolled;

        ArrayList<ILayoutElement> al = new ArrayList<>();
        CSSUtils.treeToList(this.document, al);
        ArrayList<ILayoutElement> objs = new ArrayList<>();
        for (ILayoutElement obj : al) {
            if (obj.getX() <= cx
                && cx < obj.getX() + obj.getWidth()
                && obj.getY() <= cy
                && cy < obj.getY() + obj.getHeight()) {
                objs.add(obj);
            }
        }
        if (objs.isEmpty()) {return;}
        HTMLElement elt = objs.get(objs.size()-1).getNode();
        boolean cont = true;
        while (cont) {
            if (elt instanceof HTMLText) {

            } else if (Objects.equals(((HTMLTag) elt).getTag(), "a") && ((HTMLTag) elt).getAttributes().containsKey("href")) {
                pastUrls.add(new Pair<>(this.url, this.scrolled));
                String nurl = ((HTMLTag) elt).getAttributes().get("href");
                System.out.println(nurl);
                this.url = this.url.resolve(nurl);
                System.out.println(this.url.toString());
                this.parent.updateTabName(this.getName());
                this.LoadWebPage();
                this.scrolled = 0;
                cont = false;
            }
            elt = elt.getParent();
        }

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
//        super.paintComponent(g);
//        for (Pair<Point, StyledString> renderchunk : this.layout.getDisplayList()) {
//            if (renderchunk.getFirst().y > scrolled + height) continue;
//            if (renderchunk.getFirst().y < scrolled) continue;
//            g.setFont(renderchunk.getSecond().font);
//            this.setFont(renderchunk.getSecond().font);
//            g.drawString(renderchunk.getSecond().string, renderchunk.getFirst().x, renderchunk.getFirst().y - scrolled);
//        }
        for (IDrawCmd cmd: this.displayList) {
            if (cmd.getTop() > this.scrolled + height) continue;
            if (cmd.getBottom() < this.scrolled) continue;
            cmd.execute(this, g);
        }
    }

    /*
    * def draw(self):
        self.canvas.delete("all")
        for cmd in self.display_list:
            if cmd.top > self.scroll + HEIGHT: continue
            if cmd.bottom < self.scroll: continue
            cmd.execute(self.scroll, self.canvas)
    * */

    private void paint_tree(ILayoutElement layoutElement, ArrayList<IDrawCmd> displayList) {
        displayList.addAll(layoutElement.paint());
        for (ILayoutElement child : layoutElement.getChildren()) {
            paint_tree(child, displayList);
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
            System.out.println(e);
        }
        HTMLParser parser = new HTMLParser();
        HTMLElement root = parser.parse(body);
        ArrayList<Pair<ISelector, HashMap<String, String>>> rules = (ArrayList<Pair<ISelector, HashMap<String, String>>>) Browser.getInstance().defaultStyles.clone();
        this.root = root;
        ArrayList<String> cssLinks = CSSUtils.getCSSLinks(root);
        for (String link : cssLinks) {
            URL styleURL = this.url.resolve(link);
            body = "";
            try {
                body = styleURL.Request();
            }  catch (Exception e) {
                System.out.println("Stylesheet failed to load:" + styleURL.toString());
                continue;
            }
            rules.addAll(new CSSParser(body).parse());
        }
        rules.sort(new SortByPriority());
        CSSUtils.styleNode(root, rules);
        this.document = new DocumentLayout(root);
        this.document.layout();
        this.displayList = new ArrayList<>();
        paint_tree(this.document, this.displayList);
//        parser.printTree(root, 0);
//        this.paintComponent();
//        this.layout.layout(this.root);
    }

    public Integer getScrolled() {
        return this.scrolled;
    }
}
