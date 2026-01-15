package com.github.westsi.browser.util.html;

import java.util.ArrayList;
import java.util.HashMap;

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
    void setChildren(ArrayList<HTMLElement> newChildren);
    void setStyle(HashMap<String, String> styleMap);
    void addStyle(String key, String value);
    HashMap<String, String> getStyle();
}

