package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;

import java.util.Objects;

public class TagSelector implements ISelector {
    private String tagPart = "";
    private String classPart;
    private String tag;
    private boolean hasTagPart;
    private boolean hasClassPart;
    private Integer priority;
    public TagSelector(String tag) {
        this.hasClassPart = tag.contains(".");
        this.tag = tag;
        if (!hasClassPart) this.tagPart = this.tag;
        if (tag.indexOf('.') == tag.lastIndexOf('.') && hasClassPart) {
            // make sure theres only one dot
            String[] spl = tag.split("\\.");
            tagPart = spl[0];
            classPart = spl[1];
        }
        hasTagPart = !tagPart.isEmpty();
//        System.out.println("tag=" + tag + "///tagPart=" + tagPart + "///classPart=" + classPart);
        this.priority = 1;
//        System.out.println(hasTagPart + tagPart + hasClassPart + classPart + "///" + tag);
    }

    public boolean matches(HTMLElement node) {
//        return (node instanceof HTMLTag) && (Objects.equals(((HTMLTag) node).getTag(), this.tag));
        if (!(node instanceof HTMLTag)) return false;
        if (hasTagPart) {
            if (!Objects.equals(((HTMLTag) node).getTag(), this.tagPart)) return false;
        }
        // at this point, the tag matches if it exists
        if (!hasClassPart) return true;
        String classList = ((HTMLTag) node).getAttributes().getOrDefault("class", "");
        String[] clL = classList.split(" ");
        for (String c : clL) {
            if (Objects.equals(c, this.classPart)) return true;
        }
        return false;
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        if (this.hasClassPart) {
            return this.tagPart + "--" + this.classPart + "(" + this.priority + ")";
        }
        return this.tag + "(" + this.priority + ")";
    }
}
