package com.github.westsi.browser;

import com.github.westsi.browser.util.Triplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static java.awt.BorderLayout.EAST;

public class Browser {
    private static Browser browser;
    private final JFrame frame;

    private JTabbedPane tabbedPane;

    public static Integer WIDTH = 720;
    public static Integer HEIGHT = 480;

    private final Integer refreshRate = 30;

    private ArrayList<BrowserTab> browserTabs = new ArrayList<>();

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
        tabbedPane = new JTabbedPane();
        frame.setContentPane(tabbedPane);
        frame.setJMenuBar(menuBar);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);

        frame.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                WIDTH = c.getWidth();
                HEIGHT = c.getHeight();
                for (BrowserTab bt: browserTabs) {
                    bt.updateResize();
                }
            }
        });

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
//                System.out.println("rerendering global window");
                frame.repaint();
            }
        };
        Timer timer = new Timer(1000/refreshRate, al);
        timer.start();
    }

    public static Browser getInstance() {
        if (browser == null) {
            browser = new Browser();
        }
        return browser;
    }

    public void LoadWebPage(String url) {
        BrowserTab bt = new BrowserTab(url, refreshRate, WIDTH, HEIGHT);
        browserTabs.add(bt);
        JScrollPane jsp = new JScrollPane(bt);
        jsp.setVerticalScrollBar(new JScrollBar()); // TODO: make this work
        tabbedPane.add(bt.getName(), jsp);
        bt.LoadWebPage();
    }
}
