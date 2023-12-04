package com.github.westsi.browser;

import com.github.westsi.browser.util.Triplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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

    public BrowserTab(String url, Integer refreshRate) {
        this.refreshRate = refreshRate;
        this.url = new URL(url);
    }

    @Override
    public String getName() {
        return this.url.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("rerendering tab");
        super.paintComponent(g);
        g.drawString(this.text, 100, 100);
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
//        this.displayList = this.LayoutWebPage(text);
        this.paintComponent(this.getGraphics());
    }

//    public ArrayList<Triplet<Integer, Integer, String>> LayoutWebPage(String text) {
//        ArrayList<Triplet<Integer, Integer, String>> displayList = new ArrayList<>();
//        Integer cursorX = HSTEP;
//        Integer cursorY = VSTEP;
//        for (int i=0,n=text.length(); i<n; i+= Character.charCount(text.codePointAt(i))) {
//            char ch = (char) text.codePointAt(i);
//            displayList.add(new Triplet<>(cursorX, cursorY, ((Character) ch).toString()));
//            cursorX += HSTEP;
//            if (cursorX >= this.WIDTH - HSTEP) {
//                cursorY += VSTEP;
//                cursorX = HSTEP;
//            }
//        }
//
//        return displayList;
//    }

}
