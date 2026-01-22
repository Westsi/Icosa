package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;

public class DescendantSelector implements ISelector{
    private ISelector ancestor, descendant;
    private Integer priority;
    public DescendantSelector(ISelector ancestor, ISelector descendant) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.priority = ancestor.getPriority() + descendant.getPriority();
    }

    public boolean matches(HTMLElement node) {
        if (!this.descendant.matches(node)) {return false;}
        while (node.getParent() != null) {
            if (this.ancestor.matches(node.getParent())) {return true;}
            node = node.getParent();
        }
        return false;
    }

    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return this.ancestor.toString() + "-----" + this.descendant.toString() + "-----" + this.priority;
    }
}
