package com.github.westsi.browser.util.html;

import com.github.westsi.browser.util.Pair;

import java.util.*;

/**
 * Lexes and parses source HTML into a tree of <code>HTMLElement</code>s.
 * Supports HTML Entities and self-closing tags.
 * @author Westsi
 * @version %I%
 * @see HTMLText
 * @see HTMLTag
 * @see HTMLElement
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

    private static final ArrayList<String> HEAD_TAGS = new ArrayList<>() {{
        add("base");
        add("basefont");
        add("bgsound");
        add("noscript");
        add("link");
        add("meta");
        add("title");
        add("style");
        add("script");
    }};

    private ArrayList<HTMLElement> unfinished = new ArrayList<>();

    /**
     * Parses the HTML source to the DOM Tree.
     * @param body The HTML source text to be parsed.
     * @return The root of the DOM tree which will be a <code>HTMLElement</code>.
     */
    public HTMLElement parse(String body) {
        StringBuilder buffer = new StringBuilder();
        boolean inTag = false;
        for (int i=0, n=body.length(); i<n; i+=Character.charCount(body.codePointAt(i))) {
            char ch = (char)body.codePointAt(i);

            if (ch == '<') {
                inTag = true;
                if (!buffer.toString().isBlank()) this.addText(buffer.toString());
                buffer.setLength(0);
            } else if (ch == '>') {
                inTag = false;
                this.addTag(buffer.toString());
                buffer.setLength(0);
            } else {
                buffer.append(ch);
            }
        }
        if (!inTag && !buffer.toString().isBlank()) this.addText(buffer.toString());

        return this.finish();
    }

    private void addText(String text) {
        this.implicitTags(null);
        for (Map.Entry<String, String> ent : ENTITIES.entrySet()) {
            text = text.replaceAll(ent.getKey(), ent.getValue());
        }
        HTMLElement parent = this.unfinished.get(this.unfinished.size()-1);
        HTMLText node = new HTMLText(text, parent);
        parent.addChild(node);
    }

    private void addTag(String tag) {
        Pair<String, HashMap<String, String>> attributedTag = this.getAttributes(tag);
        tag = attributedTag.getFirst();
        HashMap<String, String> attributes = attributedTag.getSecond();

        if (tag.startsWith("!")) return; // gets rid of comments and <!DOCTYPE>
        this.implicitTags(tag);

        if (tag.startsWith("/")) { // closing tag - add node to parent
            if (this.unfinished.size() == 1) return;
            HTMLElement node = this.unfinished.remove(this.unfinished.size() - 1);
            HTMLElement parent = this.unfinished.get(this.unfinished.size() - 1);
            parent.addChild(node);
        } else if (SELF_CLOSING_TAGS.contains(tag)) { // self-closing tag - add node to parent
            HTMLElement parent = this.unfinished.get(this.unfinished.size() - 1);
            HTMLElement node = new HTMLTag(tag, parent, attributes);
            parent.addChild(node);
        } else { // start of tag that has a closing tag
            HTMLElement parent = null;
            if (!this.unfinished.isEmpty()) parent = this.unfinished.get(this.unfinished.size() - 1);
            HTMLElement node = new HTMLTag(tag, parent, attributes);
            this.unfinished.add(node);
        }
    }

    private HTMLElement finish() {
        if (this.unfinished.isEmpty()) this.implicitTags(null);
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
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(text.split(" "))); // doesn't handle attributes with whitespace

        String tag = casefold(parts.remove(0));
        HashMap<String, String> attributes = new HashMap<>();

        for (String attrpair : parts) {
            if (attrpair.contains("=")) { // attribute is typical e.g. <input type="text">
                String[] spl = attrpair.split("=", 2);
                String key = casefold(spl[0]);
                String val = spl[1];
                // Strips off quotes of value
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

    private void implicitTags(String tag) {
        // this is an absolute mess and if you have to look at this I feel sorry for you
        // I'm 99% sure it won't end up in an infinite loop... WYSIWYG
        ArrayList<String> htmlCheck = new ArrayList<>() {{
            add("head");
            add("body");
            add("/html");
        }};

        while (true) {
            ArrayList<String> openTags = new ArrayList<>();
            for (HTMLElement e : this.unfinished) openTags.add(((HTMLTag) e).getTag());
            if (openTags.equals(new ArrayList<String>()) && !Objects.equals(tag, "html")) {
                this.addTag("html");
            } else if ((openTags.equals(new ArrayList<String>(){{add("html");}}) && Objects.equals(openTags.get(0), "html")) && !htmlCheck.contains(tag)) {
                if (HEAD_TAGS.contains(tag)) this.addTag("head");
                else this.addTag("body");
            } else if ((openTags.equals(new ArrayList<String>(){{add("html");add("head");}}) && (!HEAD_TAGS.contains(tag) && !Objects.equals(tag, "/head")))) {
                this.addTag("/head");
            } else break;
        }
    }
}

