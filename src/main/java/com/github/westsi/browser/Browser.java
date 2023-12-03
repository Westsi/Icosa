package com.github.westsi.browser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Browser {
    private static Browser browser;
    private JFrame frame;

    private Browser() {
        this.frame = new JFrame("Xonize Icosa");
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setResizable(true);
        frame.setLocationByPlatform(true);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.add(new JMenuItem("Reload"));
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        frame.setSize(720, 480);
    }

    public static Browser getInstance() {
        if (browser == null) {
            browser = new Browser();
        }
        return browser;
    }

    public void LoadWebPage(String requrl) {
        URL url = new URL(requrl);
        String body = "";
        try {
            body = url.Request();
        } catch (URISyntaxException e) {
            body = "<html><body><h1>Incorrect URI.</h1></body></html>";
        }  catch (InterruptedException e) {
            body = "<html><body><h1>Page load was interrupted.</h1></body></html>";
        } catch (Exception e) {
            body = "<html><body><h1>Something went wrong.</h1></body></html>";
        }
        this.RenderWebPage(body);
    }

    public void RenderWebPage(String body) {
        String text = lex(body);

        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 30, 40, 0, 500);

        JTextArea textArea = new JTextArea(3000, 50);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.append(text);

        // TODO: scrollbar renders but doesn't actually scroll anything

        frame.getContentPane().add(scrollBar, BorderLayout.EAST);
        frame.getContentPane().add(textArea);

        frame.setVisible(true);
    }
    
    private String lex(String body) {
        boolean inTag = false;
        String text = "";
        for (int i=0, n=body.length(); i<n; i+=Character.charCount(body.codePointAt(i))) {
            char ch = (char)body.codePointAt(i);
            if (ch == '<') inTag = true;
            else if (ch == '>') inTag = false;
            else if (!inTag) text = text + ch;
        }
        return text;
    }
}
