package com.github.westsi.browser.util.html;

import com.github.westsi.browser.util.Pair;

import java.util.*;

/**
 * Lexes and parses source HTML into <code>HTMLTag</code>s and <code>HTMLText</code>.
 * Supports HTML Entities.
 * @author Westsi
 * @version %I%
 * @see HTMLText
 * @see HTMLTag
 */
public class HTMLParser {
    private static final HashMap<String, String> ENTITIES = new HashMap<>() {{
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

    private static final ArrayList<String> SELF_CLOSING_TAGS = new ArrayList<>() {{
        add("area");
        add("base");
        add("br");
        add("col");
        add("embed");
        add("hr");
        add("img");
        add("input");
        add("link");
        add("meta");
        add("param");
        add("source");
        add("track");
        add("wbr");
    }};

    private ArrayList<HTMLElement> unfinished = new ArrayList<>();

    /**
     * Lexes the HTML source to its constituent elements.
     * @param body The HTML source text to be lexed.
     * @return The list of all the <code>HTMLTag</code> and <code>HTMLText</code> elements that make up the document.
     */
//    public static ArrayList<HTMLElement> lex(String body) {
//        ArrayList<HTMLElement> out = new ArrayList<>();
//        StringBuilder buffer = new StringBuilder();
//        boolean inTag = false;
//        for (int i=0, n=body.length(); i<n; i+=Character.charCount(body.codePointAt(i))) {
//            char ch = (char)body.codePointAt(i);
//
//            if (ch == '<') {
//                inTag = true;
//                if (!buffer.toString().trim().isEmpty()) out.add(new HTMLText(buffer.toString()));
//                buffer.setLength(0);
//            } else if (ch == '>') {
//                inTag = false;
//                out.add(new HTMLTag(buffer.toString()));
//                buffer.setLength(0);
//            } else {
//                buffer.append(ch);
//            }
//        }
//        for (HTMLElement e : out) {
//            try {
//                String txt = ((HTMLText) e).getText();
//                txt = txt.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//                for (Map.Entry<String, String> ent : Entities.entrySet()) {
//                    txt = txt.replaceAll(ent.getKey(), ent.getValue());
//                }
//                ((HTMLText) e).setText(txt);
//            } catch (ClassCastException ex) {}
//
//
//        }
//        if (!inTag && !buffer.toString().trim().isEmpty()) out.add(new HTMLText(buffer.toString()));
//        return out;
//    }

    public HTMLElement parse(String body) {
        StringBuilder buffer = new StringBuilder();
        boolean inTag = false;
        for (int i=0, n=body.length(); i<n; i+=Character.charCount(body.codePointAt(i))) {
            char ch = (char)body.codePointAt(i);

            if (ch == '<') {
                inTag = true;
                if (!buffer.toString().trim().isEmpty()) this.addText(buffer.toString());
                buffer.setLength(0);
            } else if (ch == '>') {
                inTag = false;
                this.addTag(buffer.toString());
                buffer.setLength(0);
            } else {
                buffer.append(ch);
            }
        }
        if (!inTag && !buffer.toString().trim().isEmpty()) this.addText(buffer.toString());

        return this.finish();
    }

    private void addText(String text) {
        HTMLElement parent = this.unfinished.get(this.unfinished.size()-1);
        HTMLText node = new HTMLText(text, parent);
        parent.addChild(node);
    }

    private void addTag(String tag) {
        Pair<String, HashMap<String, String>> attributedTag = this.getAttributes(tag);
        tag = attributedTag.getFirst();
        HashMap<String, String> attributes = attributedTag.getSecond();
        if (tag.startsWith("!")) return;
        if (tag.startsWith("/")) {
            if (this.unfinished.size() == 1) return;
            HTMLElement node = this.unfinished.remove(this.unfinished.size() - 1);
            HTMLElement parent = this.unfinished.get(this.unfinished.size() - 1);
            parent.addChild(node);
        } else if (SELF_CLOSING_TAGS.contains(tag)) {
            HTMLElement parent = this.unfinished.get(this.unfinished.size() - 1);
            HTMLElement node = new HTMLTag(tag, parent, attributes);
            parent.addChild(node);
        } else {
            HTMLElement parent = null;
            if (!this.unfinished.isEmpty()) parent = this.unfinished.get(this.unfinished.size() - 1);
            HTMLElement node = new HTMLTag(tag, parent, attributes);
            this.unfinished.add(node);
        }
    }

    private HTMLElement finish() {
        while (this.unfinished.size() > 1) {
            HTMLElement node = this.unfinished.remove(this.unfinished.size() - 1);
            HTMLElement parent = this.unfinished.get(this.unfinished.size() - 1);
            parent.addChild(node);
        }
        return this.unfinished.remove(this.unfinished.size() - 1);
    }

    public void printTree(HTMLElement node, int indent) {
        String spaces = " ".repeat(Math.max(0, indent));
        System.out.println(spaces + node);
        for (HTMLElement child: node.getChildren()) {
            printTree(child, indent + 2);
        }
    }

    private Pair<String, HashMap<String, String>> getAttributes(String text) {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(text.split(" ")));
        String tag = casefold(parts.remove(0));
        HashMap<String, String> attributes = new HashMap<>();
        for (String attrpair : parts) {
            if (attrpair.contains("=")) {
                String[] spl = attrpair.split("=", 2);
                String key = casefold(spl[0]);
                String val = spl[1];
                // Strips of quotes of value
                if (val.length() > 2 && (val.startsWith("'") || val.startsWith("\""))) {
                    val = val.substring(1, val.length() - 1);
                }
                attributes.put(key, val);
            } else { // attribute is something like <input disabled>
                attributes.put(casefold(attrpair), "");
            }
        }
        return new Pair<>(tag, attributes);
    }

    private String casefold(String s) {
        return s.toUpperCase(Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
    }
}

