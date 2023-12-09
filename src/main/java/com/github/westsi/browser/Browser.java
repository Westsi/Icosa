package com.github.westsi.browser;

import com.github.westsi.browser.util.Triplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.awt.BorderLayout.EAST;

public class Browser {
    private static Browser browser;
    private final JFrame frame;

    private JTabbedPane tabbedPane;

    public static Integer WIDTH = 720;
    public static Integer HEIGHT = 480;

    private final Integer refreshRate = 30;


    public static HashMap<Integer, Font> fonts = new HashMap<>();

    static {
        try {
            fonts.put(Font.PLAIN, Font.createFont(Font.TRUETYPE_FONT, Browser.class.getResourceAsStream("/Arial.ttf")).deriveFont(Font.PLAIN));
            fonts.put(Font.ITALIC, Font.createFont(Font.TRUETYPE_FONT, Browser.class.getResourceAsStream("/Arial_Italic.ttf")).deriveFont(Font.ITALIC));
            fonts.put(Font.BOLD, Font.createFont(Font.TRUETYPE_FONT, Browser.class.getResourceAsStream("/Arial_Bold.ttf")).deriveFont(Font.BOLD));
            fonts.put(Font.BOLD | Font.ITALIC, Font.createFont(Font.TRUETYPE_FONT, Browser.class.getResourceAsStream("/Arial_Bold_Italic.ttf")).deriveFont(Font.BOLD | Font.ITALIC));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            fonts.put(Font.PLAIN, new Font(Font.SANS_SERIF, Font.PLAIN, 12).deriveFont(Font.PLAIN));
            fonts.put(Font.ITALIC, new Font(Font.SANS_SERIF, Font.ITALIC, 12).deriveFont(Font.ITALIC));
            fonts.put(Font.BOLD, new Font(Font.SANS_SERIF, Font.BOLD, 12).deriveFont(Font.BOLD));
            fonts.put(Font.BOLD | Font.ITALIC, new Font(Font.SANS_SERIF, Font.BOLD, 12).deriveFont(Font.BOLD | Font.ITALIC));
        }
    }


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
        frame.setFont(fonts.get(Font.PLAIN));

        frame.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                if (Math.abs(c.getWidth()-WIDTH) > 100 || Math.abs(c.getHeight()-HEIGHT) > 100) {
                    WIDTH = c.getWidth();
                    HEIGHT = c.getHeight();
                    for (BrowserTab bt: browserTabs) {
                        bt.updateResize();
                    }
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
        BrowserTab bt = new BrowserTab(url, WIDTH, HEIGHT);
        browserTabs.add(bt);
        JScrollPane jsp = new JScrollPane(bt);
        jsp.setVerticalScrollBar(new JScrollBar()); // TODO: make this work
        tabbedPane.add(bt.getName(), jsp);
        bt.LoadWebPage();
    }
}
