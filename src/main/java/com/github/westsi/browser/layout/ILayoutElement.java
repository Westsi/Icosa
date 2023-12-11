package com.github.westsi.browser.layout;

import com.github.westsi.browser.util.html.HTMLElement;

import java.util.ArrayList;

public interface ILayoutElement {
    HTMLElement getNode();
    ILayoutElement getParent();
    ILayoutElement getPrevious();
    ArrayList<ILayoutElement> getChildren();
}
