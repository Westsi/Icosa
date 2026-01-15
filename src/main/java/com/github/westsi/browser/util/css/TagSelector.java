package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;

import java.util.Objects;

public class TagSelector implements ISelector {
    private String tag;
    private Integer priority;
    public TagSelector(String tag) {
        this.tag = tag;
        this.priority = 1;
    }

    public boolean matches(HTMLElement node) {
        return (node instanceof HTMLTag) && (Objects.equals(((HTMLTag) node).getTag(), this.tag));
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
