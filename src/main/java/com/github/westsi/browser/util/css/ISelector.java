package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;

public interface ISelector {
    public boolean matches(HTMLElement node);
    public Integer getPriority();
}
