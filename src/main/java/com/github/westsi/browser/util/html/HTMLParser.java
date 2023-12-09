package com.github.westsi.browser.util.html;

import java.util.ArrayList;

public class HTMLParser {
    public static ArrayList<HTMLElement> lex(String body) {
        ArrayList<HTMLElement> out = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean inTag = false;
        for (int i=0, n=body.length(); i<n; i+=Character.charCount(body.codePointAt(i))) {
            char ch = (char)body.codePointAt(i);

            if (ch == '<') {
                inTag = true;
                if (!buffer.toString().trim().isEmpty()) out.add(new HTMLText(buffer.toString()));
                buffer.setLength(0);
            } else if (ch == '>') {
                inTag = false;
                out.add(new HTMLTag(buffer.toString()));
                buffer.setLength(0);
            } else {
                buffer.append(ch);
            }
        }
        if (!inTag && !buffer.toString().trim().isEmpty()) out.add(new HTMLText(buffer.toString()));
        return out;
    }
}

