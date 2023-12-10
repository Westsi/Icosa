package com.github.westsi.browser.util.html;

import java.util.ArrayList;

/**
 * HTML Element interface to use as lexed HTML tokens.
 * @author Westsi
 * @version %I%
 * @see HTMLTag
 * @see HTMLText
 */
public interface HTMLElement {
    String toString();
    HTMLElement getParent();
    ArrayList<HTMLElement> getChildren();
    void addChild(HTMLElement child);
}

