package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;

import java.util.Objects;

public class TagSelector implements ISelector {

    private String tagPart;
    private String classPart;
    private String idPart;

    private boolean hasTagPart;
    private boolean hasClassPart;
    private boolean hasIdPart;

    private final int priority;

    public TagSelector(String selector) {
        parse(selector);

        int p = 0;
        if (hasIdPart) p += 100;
        if (hasClassPart) p += 10;
        if (hasTagPart) p += 1;
        this.priority = p;
    }

    private void parse(String selector) {
        String rest = selector;

        // tag (must be first if present)
        int cut = indexOfAny(rest, '.', '#');
        if (cut == -1) {
            tagPart = rest;
            rest = "";
        } else if (cut > 0) {
            tagPart = rest.substring(0, cut);
            rest = rest.substring(cut);
        }

        // id
        int idIdx = rest.indexOf('#');
        if (idIdx != -1) {
            int end = indexOfAny(rest.substring(idIdx + 1), '.', '#');
            if (end == -1) {
                idPart = rest.substring(idIdx + 1);
            } else {
                idPart = rest.substring(idIdx + 1, idIdx + 1 + end);
            }
        }

        // class
        int classIdx = rest.indexOf('.');
        if (classIdx != -1) {
            int end = indexOfAny(rest.substring(classIdx + 1), '.', '#');
            if (end == -1) {
                classPart = rest.substring(classIdx + 1);
            } else {
                classPart = rest.substring(classIdx + 1, classIdx + 1 + end);
            }
        }

        hasTagPart = tagPart != null && !tagPart.isEmpty();
        hasIdPart = idPart != null && !idPart.isEmpty();
        hasClassPart = classPart != null && !classPart.isEmpty();
    }

    private int indexOfAny(String s, char... chars) {
        int best = -1;
        for (char c : chars) {
            int i = s.indexOf(c);
            if (i != -1 && (best == -1 || i < best)) {
                best = i;
            }
        }
        return best;
    }

    @Override
    public boolean matches(HTMLElement node) {
        if (!(node instanceof HTMLTag tag)) return false;
//        if (Objects.equals(classPart, "links") && Objects.equals(tag.getTag(), "nav")) {
//            System.out.println();
//            System.out.println("tagPart: " + hasTagPart + tagPart);
//            System.out.println("idPart: " + hasIdPart + idPart);
//            System.out.println("classPart: " + hasClassPart + classPart);
//            System.out.println("id node " + tag.getAttributes().getOrDefault("id", "ICOSAERR"));
//        }

        if (hasTagPart && !Objects.equals(tag.getTag(), tagPart)) return false;

        if (hasIdPart) {
            String id = tag.getAttributes().getOrDefault("id", "");
            if (!Objects.equals(id, idPart)) return false;
        }

        if (hasClassPart) {
            String classList = tag.getAttributes().getOrDefault("class", "");
            for (String c : classList.trim().split("\\s+")) {
                if (Objects.equals(c, classPart) && !c.isEmpty()) return true;
            }
            return false;
        }

        return true;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return (hasTagPart ? tagPart : "")
                + (hasIdPart ? "#" + idPart : "")
                + (hasClassPart ? "." + classPart : "")
                + " (" + priority + ")";
    }
}
