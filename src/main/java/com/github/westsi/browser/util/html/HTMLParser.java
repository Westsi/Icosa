package com.github.westsi.browser.util.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Lexes and parses source HTML into <code>HTMLTag</code>s and <code>HTMLText</code>.
 * Supports HTML Entities.
 * @author Westsi
 * @version %I%
 * @see HTMLText
 * @see HTMLTag
 */
public class HTMLParser {
    private static final HashMap<String, String> Entities = new HashMap<>() {{
        put("&lt;", "<");
        put("&gt;", ">");
        put("&amp;", "&");
        put("&quot;", "\"");
        put("&apos;", "'");
        put("&cent;", "¢");
        put("&pound;", "£");
        put("&yen;", "¥");
        put("&euro;", "€");
        put("&copy;", "©");
        put("&reg;", "®");

    }};

    /**
     * Lexes the HTML source to its constituent elements.
     * @param body The HTML source text to be lexed.
     * @return The list of all the <code>HTMLTag</code> and <code>HTMLText</code> elements that make up the document.
     */
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
        for (HTMLElement e : out) {
            try {
                String txt = ((HTMLText) e).getText();
                txt = txt.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                for (Map.Entry<String, String> ent : Entities.entrySet()) {
                    txt = txt.replaceAll(ent.getKey(), ent.getValue());
                }
                ((HTMLText) e).setText(txt);
            } catch (ClassCastException ex) {}


        }
        if (!inTag && !buffer.toString().trim().isEmpty()) out.add(new HTMLText(buffer.toString()));
        return out;
    }
}

