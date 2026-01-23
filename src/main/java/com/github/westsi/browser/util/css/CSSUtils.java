package com.github.westsi.browser.util.css;

import com.github.westsi.browser.layout.ILayoutElement;
import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;
import com.github.westsi.browser.util.html.HTMLText;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CSSUtils {
    public static final HashMap<String, String> COLOR_MAP = createColorMap();
    public static final HashMap<String, String> INHERITED_PROPERTIES = createInheritedProperties();
    public static void styleNode(HTMLElement node, ArrayList<Pair<ISelector, HashMap<String, String>>> rules) {
        node.setStyle(new HashMap<>());

        // apply inherited properties
//        for (String prop : INHERITED_PROPERTIES.keySet()) {
//            if (node.getParent() != null) {
//                node.addStyle(prop, node.getParent().getStyle().get(prop));
//            } else {
//                node.addStyle(prop, INHERITED_PROPERTIES.get(prop));
//            }
//        }

        // to try to make it so everything is inherited from parents
        if (node.getParent() != null) {
            for (String prop : node.getParent().getStyle().keySet()) {
                node.addStyle(prop, node.getParent().getStyle().get(prop));
            }
        } else {
            for (String prop : INHERITED_PROPERTIES.keySet()) {
                node.addStyle(prop, INHERITED_PROPERTIES.get(prop));
            }
        }

        // now apply rules
        for (Pair<ISelector, HashMap<String, String>> pair : rules) {
//            if (node instanceof HTMLText && (node.getParent() instanceof HTMLTag) && ((HTMLTag)node.getParent()).getTag().equals("a")) {
//                System.out.println(pair.getFirst() + "///" + pair.getSecond() + "/////" + node + "---MATCHES? = " + pair.getFirst().matches(node));
//            }
            if (!pair.getFirst().matches(node)) {continue;}
            if (pair.getSecond().containsKey("outline")) {
                System.out.println(pair.getFirst() + "//OUTLINE");
            }
            for (String property : pair.getSecond().keySet()) {
                node.addStyle(property, pair.getSecond().get(property));
            }
        }


        if (node instanceof HTMLTag) {
//            System.out.println(((HTMLTag) node).getAttributes().containsKey("style"));
        }
        if (node instanceof HTMLTag && ((HTMLTag) node).getAttributes().containsKey("style")) {
            HashMap<String, String> pairs = new CSSParser(((HTMLTag) node).getAttributes().get("style")).body();
            for (Map.Entry<String,String> entry : pairs.entrySet()) {
                node.addStyle(entry.getKey(), entry.getValue());
            }
        }
        if (node.getStyle().get("font-size").endsWith("%")) {
            String parentFontSize = "";
            if (node.getParent() != null) {
                parentFontSize = node.getParent().getStyle().get("font-size");
            } else {
                parentFontSize = INHERITED_PROPERTIES.get("font-size");
            }
            float nodePct = Float.parseFloat(node.getStyle().get("font-size")
                    .substring(0, node.getStyle().get("font-size").length()-1)) / 100;
            float parentPx = Float.parseFloat(parentFontSize.substring(0, parentFontSize.length()-2));
            node.addStyle("font-size", (nodePct*parentPx)+"px");
        }
        for (HTMLElement child : node.getChildren()) {
            styleNode(child, rules);
        }
    }

    public static Color cssColorToJava(String color) {
        if (color == null) {
            // BRIGHT GREEN FOR PANIC
            return new Color(0, 255, 0, 255);
        }
        // string color could be hex or a string
        // if hex it starts with #
        if (!(color.charAt(0) == '#')) {
            // it is a css name e.g. lightblue
            // default to the same green as above
            color = COLOR_MAP.getOrDefault(color, "#00ff00");
        }
        if (color.charAt(0) == '#') {
            // is it a RGB (#xxxxxx length = 7) or RGBA (#xxxxxxxx length = 9) or bastardized RGB (#fff, which is #ffffff)?
            Integer r, g, b, a = 255;
            if (color.length() == 9) {
                a = Integer.decode("#"+color.substring(7));
            }
            if (color.length() == 4) {
                r = Integer.decode("#"+color.charAt(1)+color.charAt(1));
                g = Integer.decode("#"+color.charAt(2)+color.charAt(2));
                b = Integer.decode("#"+color.charAt(3)+color.charAt(3));
            } else {
                r = Integer.decode("#"+color.substring(1, 3));
                g = Integer.decode("#"+color.substring(3, 5));
                b = Integer.decode("#"+color.substring(5, 7));
            }
            return new Color(r, g, b, a);
        }
        return new Color(255, 0, 0, 0);
    }

//    def tree_to_list(tree, list):
//            list.append(tree)
//            for child in tree.children:
//    tree_to_list(child, list)
//    return list

    public static ArrayList<HTMLElement> treeToList(HTMLElement tree, ArrayList<HTMLElement> list) {
        list.add(tree);
        for (HTMLElement child : tree.getChildren()) {
            treeToList(child, list);
        }
        return list;
    }

    public static ArrayList<ILayoutElement> treeToList(ILayoutElement tree, ArrayList<ILayoutElement> list) {
        list.add(tree);
        for (ILayoutElement child : tree.getChildren()) {
            treeToList(child, list);
        }
        return list;
    }

    public static ArrayList<String> getCSSLinks(HTMLElement nodes) {
        ArrayList<String> links = new ArrayList<>();
//        links = [node.attributes["href"]
//        for node in tree_to_list(self.nodes, [])
//        if isinstance(node, Element)
//        and node.tag == "link"
//        and node.attributes.get("rel") == "stylesheet"
//        and "href" in node.attributes]
        for (HTMLElement node : treeToList(nodes, new ArrayList<>())) {
            if (node instanceof HTMLTag
                    && Objects.equals(((HTMLTag) node).getTag(), "link")
                    && Objects.equals(((HTMLTag) node).getAttributes().get("rel"), "stylesheet")
                    && ((HTMLTag) node).getAttributes().containsKey("href")) {
                links.add(((HTMLTag) node).getAttributes().get("href"));
            }
        }
        return links;
    }

    private static HashMap<String, String> createInheritedProperties() {
        HashMap<String, String> m = new HashMap<>();
        m.put("font-size", "16px");
        m.put("font-style", "normal");
        m.put("font-weight", "normal");
        m.put("color", "black");
        return m;
    }

    private static HashMap<String, String> createColorMap() {
        HashMap<String, String> m = new HashMap<>();
        m.put("aliceblue", "#f0f8ff");
        m.put("antiquewhite", "#faebd7");
        m.put("aqua", "#00ffff");
        m.put("aquamarine", "#7fffd4");
        m.put("azure", "#f0ffff");
        m.put("beige", "#f5f5dc");
        m.put("bisque", "#ffe4c4");
        m.put("black", "#000000");
        m.put("blanchedalmond", "#ffebcd");
        m.put("blue", "#0000ff");
        m.put("blueviolet", "#8a2be2");
        m.put("brown", "#a52a2a");
        m.put("burlywood", "#deb887");
        m.put("cadetblue", "#5f9ea0");
        m.put("chartreuse", "#7fff00");
        m.put("chocolate", "#d2691e");
        m.put("coral", "#ff7f50");
        m.put("cornflowerblue", "#6495ed");
        m.put("cornsilk", "#fff8dc");
        m.put("crimson", "#dc143c");
        m.put("cyan", "#00ffff");
        m.put("darkblue", "#00008b");
        m.put("darkcyan", "#008b8b");
        m.put("darkgoldenrod", "#b8860b");
        m.put("darkgray", "#a9a9a9");
        m.put("darkgrey", "#a9a9a9");
        m.put("darkgreen", "#006400");
        m.put("darkkhaki", "#bdb76b");
        m.put("darkmagenta", "#8b008b");
        m.put("darkolivegreen", "#556b2f");
        m.put("darkorange", "#ff8c00");
        m.put("darkorchid", "#9932cc");
        m.put("darkred", "#8b0000");
        m.put("darksalmon", "#e9967a");
        m.put("darkseagreen", "#8fbc8f");
        m.put("darkslateblue", "#483d8b");
        m.put("darkslategray", "#2f4f4f");
        m.put("darkslategrey", "#2f4f4f");
        m.put("darkturquoise", "#00ced1");
        m.put("darkviolet", "#9400d3");
        m.put("deeppink", "#ff1493");
        m.put("deepskyblue", "#00bfff");
        m.put("dimgray", "#696969");
        m.put("dimgrey", "#696969");
        m.put("dodgerblue", "#1e90ff");
        m.put("firebrick", "#b22222");
        m.put("floralwhite", "#fffaf0");
        m.put("forestgreen", "#228b22");
        m.put("fuchsia", "#ff00ff");
        m.put("gainsboro", "#dcdcdc");
        m.put("ghostwhite", "#f8f8ff");
        m.put("gold", "#ffd700");
        m.put("goldenrod", "#daa520");
        m.put("gray", "#808080");
        m.put("grey", "#808080");
        m.put("green", "#008000");
        m.put("greenyellow", "#adff2f");
        m.put("honeydew", "#f0fff0");
        m.put("hotpink", "#ff69b4");
        m.put("indianred", "#cd5c5c");
        m.put("indigo", "#4b0082");
        m.put("ivory", "#fffff0");
        m.put("khaki", "#f0e68c");
        m.put("lavender", "#e6e6fa");
        m.put("lavenderblush", "#fff0f5");
        m.put("lawngreen", "#7cfc00");
        m.put("lemonchiffon", "#fffacd");
        m.put("lightblue", "#add8e6");
        m.put("lightcoral", "#f08080");
        m.put("lightcyan", "#e0ffff");
        m.put("lightgoldenrodyellow", "#fafad2");
        m.put("lightgray", "#d3d3d3");
        m.put("lightgrey", "#d3d3d3");
        m.put("lightgreen", "#90ee90");
        m.put("lightpink", "#ffb6c1");
        m.put("lightsalmon", "#ffa07a");
        m.put("lightseagreen", "#20b2aa");
        m.put("lightskyblue", "#87cefa");
        m.put("lightslategray", "#778899");
        m.put("lightslategrey", "#778899");
        m.put("lightsteelblue", "#b0c4de");
        m.put("lightyellow", "#ffffe0");
        m.put("lime", "#00ff00");
        m.put("limegreen", "#32cd32");
        m.put("linen", "#faf0e6");
        m.put("magenta", "#ff00ff");
        m.put("maroon", "#800000");
        m.put("mediumaquamarine", "#66cdaa");
        m.put("mediumblue", "#0000cd");
        m.put("mediumorchid", "#ba55d3");
        m.put("mediumpurple", "#9370db");
        m.put("mediumseagreen", "#3cb371");
        m.put("mediumslateblue", "#7b68ee");
        m.put("mediumspringgreen", "#00fa9a");
        m.put("mediumturquoise", "#48d1cc");
        m.put("mediumvioletred", "#c71585");
        m.put("midnightblue", "#191970");
        m.put("mintcream", "#f5fffa");
        m.put("mistyrose", "#ffe4e1");
        m.put("moccasin", "#ffe4b5");
        m.put("navajowhite", "#ffdead");
        m.put("navy", "#000080");
        m.put("oldlace", "#fdf5e6");
        m.put("olive", "#808000");
        m.put("olivedrab", "#6b8e23");
        m.put("orange", "#ffa500");
        m.put("orangered", "#ff4500");
        m.put("orchid", "#da70d6");
        m.put("palegoldenrod", "#eee8aa");
        m.put("palegreen", "#98fb98");
        m.put("paleturquoise", "#afeeee");
        m.put("palevioletred", "#db7093");
        m.put("papayawhip", "#ffefd5");
        m.put("peachpuff", "#ffdab9");
        m.put("peru", "#cd853f");
        m.put("pink", "#ffc0cb");
        m.put("plum", "#dda0dd");
        m.put("powderblue", "#b0e0e6");
        m.put("purple", "#800080");
        m.put("rebeccapurple", "#663399");
        m.put("red", "#ff0000");
        m.put("rosybrown", "#bc8f8f");
        m.put("royalblue", "#4169e1");
        m.put("saddlebrown", "#8b4513");
        m.put("salmon", "#fa8072");
        m.put("sandybrown", "#f4a460");
        m.put("seagreen", "#2e8b57");
        m.put("seashell", "#fff5ee");
        m.put("sienna", "#a0522d");
        m.put("silver", "#c0c0c0");
        m.put("skyblue", "#87ceeb");
        m.put("slateblue", "#6a5acd");
        m.put("slategray", "#708090");
        m.put("slategrey", "#708090");
        m.put("snow", "#fffafa");
        m.put("springgreen", "#00ff7f");
        m.put("steelblue", "#4682b4");
        m.put("tan", "#d2b48c");
        m.put("teal", "#008080");
        m.put("thistle", "#d8bfd8");
        m.put("tomato", "#ff6347");
        m.put("turquoise", "#40e0d0");
        m.put("violet", "#ee82ee");
        m.put("wheat", "#f5deb3");
        m.put("white", "#ffffff");
        m.put("whitesmoke", "#f5f5f5");
        m.put("yellow", "#ffff00");
        m.put("yellowgreen", "#9acd32");
        return m;
    }
}
