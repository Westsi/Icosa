package com.github.westsi.browser.layout;

import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.StyledString;
import com.github.westsi.browser.util.html.HTMLElement;

import java.awt.*;
import java.util.ArrayList;

public interface ILayoutElement {
    HTMLElement getNode();
    ILayoutElement getParent();
    ILayoutElement getPrevious();
    ArrayList<ILayoutElement> getChildren();
    void layout();
    ArrayList<Pair<Point, StyledString>> getDisplayList();
}
