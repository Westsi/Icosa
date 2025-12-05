package com.github.westsi.browser.layout.drawn;

import com.github.westsi.browser.BrowserTab;

import java.awt.*;

public interface IDrawCmd {
    Integer getTop();
    Integer getBottom();
    void execute(BrowserTab ctx, Graphics g);
}
