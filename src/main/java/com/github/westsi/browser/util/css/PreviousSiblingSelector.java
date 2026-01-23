package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;

public class PreviousSiblingSelector implements ISelector{
    private ISelector sib1, sib2;
    private Integer priority;
    public PreviousSiblingSelector(ISelector sib1, ISelector sib2) {
        this.sib1 = sib1;
        this.sib2 = sib2;
        this.priority = sib1.getPriority() + sib2.getPriority();
        // div ~ p {... => sib1 = div; sib2 = p
    }
    @Override
    public boolean matches(HTMLElement node) {
        if (!sib2.matches(node)) return false;
        int thisIndex = node.getParent().getChildren().indexOf(node);
        for (int i = 0;i<thisIndex;i++) {
            if (sib1.matches(node.getParent().getChildren().get(i))) return true;
        }
        return false;
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "PreviousSiblingSelector: " + this.sib1.toString() + " ~ " + this.sib2.toString() + "-----" + this.priority;
    }
}
