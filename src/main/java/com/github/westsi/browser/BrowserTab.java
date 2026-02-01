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
import com.github.westsi.browser.util.url.URLType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    protected Integer scrolled = 0;
    private URL url;
//    private final OldLayout layout;
    private HTMLElement root;
    private Integer width;
    private Integer height;
    private ArrayList<IDrawCmd> displayList;
    private DocumentLayout document;
    protected ArrayList<URL> urls = new ArrayList<>();
    protected Integer currentUrlIndex = 0;
    protected Browser parent;
    private String title;

    public BrowserTab(String url, Integer width, Integer height, Browser parent) {
        this.url = new URL(url);
        this.urls.add(this.url);
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
            scrolled = Math.min(Math.max(0, scrolled), this.document.getHeight()+Browser.getInstance().HEIGHT);
//            System.out.println(message + scrolled);
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
        this.width = Browser.getInstance().WIDTH - 50; // frame boundaries + extra crap
        this.height = Browser.getInstance().HEIGHT - 100; // frame boundaries + extra crap

        // issue with stuff being rendered twice - i think this is from children not being cleared.
        // temporary fix - possibly bad performance in the future
        DocumentLayout newd = new DocumentLayout(this.document.getNode());
        this.document = newd;
        //
        this.document.setWidth(this.width-50);
        this.document.layout();
        this.displayList = new ArrayList<>();
        paint_tree(this.document, this.displayList);
//        this.layout.updateResize(this.width, this.height);
//        this.layout.layout(this.root);
    }

    protected void navigateUpdates(Integer scrolled) {
        this.LoadWebPage();
        this.title = null;
        this.parent.updateTabName(this.getName());
        this.scrolled = scrolled;
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
            if (elt == null) {
                return;
            }
            if (elt instanceof HTMLText) {

            } else if (Objects.equals(((HTMLTag) elt).getTag(), "a") && ((HTMLTag) elt).getAttributes().containsKey("href")) {
                String nurl = ((HTMLTag) elt).getAttributes().get("href");
                System.out.println(nurl);
                if (nurl.startsWith("#")) {
                    this.scrollToId(nurl.substring(1));
                    return;
                }
                this.url = this.url.resolve(nurl);
                urls.add(this.url);
                currentUrlIndex++;
                System.out.println(this.url.toString());
                this.navigateUpdates(0);
                cont = false;
            }
            elt = elt.getParent();
        }

    }

    private void scrollToId(String id) {
//        System.out.println("old scroll " + scrolled);
        ArrayList<ILayoutElement> al = new ArrayList<>();
        CSSUtils.treeToList(document, al);
        for (ILayoutElement el : al) {
            if (el.getNode() instanceof HTMLTag) {
                if (((HTMLTag) el.getNode()).getAttributes().getOrDefault("id", "").equals(id)) {
//                    System.out.println(el);
                    scrolled = el.getY();
                }
            }
        }
//        System.out.println("new scroll " + scrolled);
    }

    @Override
    public Graphics getGraphics() {
        return super.getGraphics();
    }

    @Override
    public String getName() {
        if (this.root == null) {
            this.title = this.url.toString();
        }
        // if root is not null
        if (this.title == null) {
            ArrayList<HTMLElement> al = new ArrayList<>();
            CSSUtils.treeToList(this.root, al);
            for (HTMLElement he : al) {
                if (he instanceof HTMLTag) {
                    if (((HTMLTag) he).getTag().equals("title")) {
                        HTMLText titleCont = (HTMLText) he.getChildren().get(0);
                        System.out.println(he.getChildren());
                        this.title = titleCont.getText();
                        break;
                    }
                }
            }
        }
        // this is if root is not null but there are no title elements
        if (this.title == null) {
            this.title = this.url.toString();
        }
//        System.out.println("Called getName()" + this.title);
        return this.title;
    }

    public URL getUrl() {
        return url;
    }
    protected void setUrl(URL nurl) {
        this.url = nurl;
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
            System.out.println("loading css link: " + styleURL);
            body = "";
            try {
                body = styleURL.Request();
            }  catch (Exception e) {
                System.out.println("Stylesheet failed to load:" + styleURL.toString());
                continue;
            }
            ArrayList<Pair<ISelector, HashMap<String, String>>> ssRules = new CSSParser(body).parse();
            System.out.println(ssRules);
            rules.addAll(ssRules);
        }
        rules.sort(new SortByPriority());
        System.out.println(rules);
        CSSUtils.styleNode(root, rules);
        this.document = new DocumentLayout(root);
        // TODO: apply css stuff like bottom/top/left/right/margin/padding on layout
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
