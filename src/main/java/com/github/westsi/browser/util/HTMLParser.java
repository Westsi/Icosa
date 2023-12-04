package com.github.westsi.browser.util;

public class HTMLParser {

    public static String lex(String body) {
        boolean inTag = false;
        StringBuilder text = new StringBuilder();
        for (int i=0, n=body.length(); i<n; i+=Character.charCount(body.codePointAt(i))) {
            char ch = (char)body.codePointAt(i);
            if (ch == '<') inTag = true;
            else if (ch == '>') inTag = false;
            else if (!inTag) text.append(ch);
        }
        return text.toString();
    }
}
