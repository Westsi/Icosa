package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;

public class DirectDescendantSelector implements ISelector {
    private ISelector ancestor, descendant;
    private Integer priority;
    public DirectDescendantSelector(ISelector ancestor, ISelector descendant) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.priority = ancestor.getPriority() + descendant.getPriority();
    }
    @Override
    public boolean matches(HTMLElement node) {
        // only matches if the parent is ancestor and the node is descendant
        if (!this.descendant.matches(node)) {return false;}
        if (!this.ancestor.matches(node.getParent())) {return false;}
        return true;
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "DirectDescendant: " + this.ancestor.toString() + " > " +  this.descendant.toString() + "-----" + this.priority;
    }
}
