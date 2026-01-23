package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;

public class AllDescendantSelector implements ISelector {
    private ISelector ancestor;
    private Integer priority;
    public AllDescendantSelector(ISelector ancestor) {
        this.ancestor = ancestor;
        this.priority = ancestor.getPriority();
    }
    @Override
    public boolean matches(HTMLElement node) {
        // only matches if some ancestor of the node matches this.ancestor
        while (node.getParent() != null) {
            if (this.ancestor.matches(node.getParent())) {return true;}
            node = node.getParent();
        }
        return false;
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "AllDescendantSelector: " + this.ancestor.toString() + " * -----" + this.priority;
    }
}
