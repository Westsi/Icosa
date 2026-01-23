package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;

public class AdjacentSiblingSelector implements ISelector {
    private ISelector sib1, sib2;
    private Integer priority;
    public AdjacentSiblingSelector(ISelector sib1, ISelector sib2) {
        this.sib1 = sib1;
        this.sib2 = sib2;
        this.priority = sib1.getPriority() + sib2.getPriority();
        // div + p {... => sib1 = div; sib2 = p
    }
    @Override
    public boolean matches(HTMLElement node) {
        if (node.getParent() == null) return false;
        if (!sib2.matches(node)) return false;
        int thisIndex = node.getParent().getChildren().indexOf(node);
        if (thisIndex == 0) return false;
        int s1Index = thisIndex-1; // i think this should be correct
        if (!sib1.matches(node.getParent().getChildren().get(s1Index))) return false;
        return true;
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "AdjacentSiblingSelector: " + this.sib1.toString() + " + " + this.sib2.toString() + "-----" + this.priority;
    }
}
