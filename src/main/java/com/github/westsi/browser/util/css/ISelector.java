package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;

public interface ISelector {
    boolean matches(HTMLElement node);
    Integer getPriority();
    @Override
    String toString();
}
