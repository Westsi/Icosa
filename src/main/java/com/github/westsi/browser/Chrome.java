package com.github.westsi.browser;

import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.url.URL;

import javax.swing.*;
import java.awt.*;

public class Chrome {

    private JTextField urlBar;
    private JButton backButton;
    private JButton forwardButton;
    private JButton newTabButton;
    private JButton reloadButton;

    private JPanel tabPanel;
    private Integer activeTab;

    private JPanel browserPane;

    public Chrome(JPanel browserPane) {
        this.browserPane = browserPane;
        createChromeUI();
        activeTab = 0;
    }

    private void createChromeUI() {
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BorderLayout());

        // Tabs row
        tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
//        refreshTabButtons();

        // Navigation bar
        JPanel navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.X_AXIS));

        backButton = new JButton("←");
        forwardButton = new JButton("→");
        reloadButton = new JButton("⟳");
        newTabButton = new JButton("+");
        urlBar = new JTextField();

        backButton.addActionListener(e -> goBack(activeTab));
        forwardButton.addActionListener(e -> goForward(activeTab));
        reloadButton.addActionListener(e -> reload(activeTab));
        newTabButton.addActionListener(e -> makeNewTab());
        urlBar.addActionListener(e -> {
            String text = urlBar.getText();
//            System.out.println("Enter pressed: " + text);
            BrowserTab ct = Browser.getInstance().browserTabs.get(activeTab);
            URL nurl = new URL(text);
            ct.urls.add(nurl);
            ct.currentUrlIndex++;
            ct.setUrl(nurl);
            ct.navigateUpdates(0);
        });

        navBar.add(backButton);
        navBar.add(forwardButton);
        navBar.add(reloadButton);
        navBar.add(newTabButton);
        navBar.add(Box.createHorizontalStrut(10));
        navBar.add(urlBar);

        topContainer.add(tabPanel, BorderLayout.NORTH);
        topContainer.add(navBar, BorderLayout.CENTER);

        browserPane.add(topContainer, BorderLayout.NORTH);
    }

    protected void setActiveTab(Integer at) {
        activeTab = at;
        if (!Browser.getInstance().browserTabs.isEmpty() && at >= 0) {
            String title = Browser.getInstance().browserTabs.get(at).getName();
            String url = Browser.getInstance().browserTabs.get(at).getUrl().toString();
            if (!urlBar.isFocusOwner()) {
                urlBar.setText(url);
            }
            Browser.getInstance().frame.setTitle("Xonize Icosa - " + title);
        }
    }

    private void goBack(Integer at) {
        BrowserTab bt = Browser.getInstance().browserTabs.get(at);
        if (!bt.urls.isEmpty() && bt.currentUrlIndex > 0) {
            URL pu = bt.urls.get(bt.currentUrlIndex-1);
            bt.currentUrlIndex--;
            bt.setUrl(pu);
            bt.navigateUpdates(0);
        }
    }
    private void goForward(Integer at) {
        BrowserTab bt = Browser.getInstance().browserTabs.get(at);
        if (bt.currentUrlIndex < bt.urls.size()-1) {
            URL pu = bt.urls.get(bt.currentUrlIndex+1);
            bt.currentUrlIndex++;
            bt.setUrl(pu);
            bt.navigateUpdates(0);
        }
    }
    private void reload(Integer at) {
        BrowserTab bt = Browser.getInstance().browserTabs.get(at);
        bt.navigateUpdates(bt.scrolled);
    }

    private void makeNewTab() {
        BrowserTab nbt = new BrowserTab("icosa://home",Browser.getInstance().WIDTH, Browser.getInstance().HEIGHT, Browser.getInstance());
        Browser.getInstance().browserTabs.add(nbt);
        JScrollPane jsp = new JScrollPane(nbt);
        jsp.setVerticalScrollBar(new JScrollBar()); // TODO: make this work
        nbt.LoadWebPage();
        Browser.getInstance().tabbedPane.add(nbt.getName(), jsp);
    }
}
