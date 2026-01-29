package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.Pair;
import com.github.westsi.browser.util.html.HTMLElement;
import com.github.westsi.browser.util.html.HTMLTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CSSParser {
    private String s;
    private Integer i;
    public CSSParser(String s) {
        this.s = s;
        this.i = 0;
    }

    private void whitespace() {
//        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
//        StackTraceElement e = stacktrace[2];  //maybe this number needs to be corrected
//        String methodName = e.getMethodName();
//        System.out.println("whitespace called from " + methodName);
        while (this.i < this.s.length()) {
            if (Character.isWhitespace(this.s.charAt(this.i))) {
                this.i++;
            } else if (this.i + 1 < this.s.length()
                    && this.s.charAt(this.i) == '/'
                    && this.s.charAt(this.i + 1) == '*') {
                skipComments();
            } else {
                break;
            }
        }
//        System.out.println("i at end of whitespace " + this.i + ". length = " + this.s.length());
//        System.out.println(this.s.substring(Math.max(this.i-50,0), this.i) + "####" + this.s.substring(this.i, Math.min(this.i+100, this.s.length())));
    }

    private void skipComments() {
//        System.out.println("skipping comments");
        while (this.i + 1 < this.s.length()
                && this.s.charAt(this.i) == '/'
                && this.s.charAt(this.i + 1) == '*') {

            this.i += 2;
            while (this.i + 1 < this.s.length()
                    && !(this.s.charAt(this.i) == '*' && this.s.charAt(this.i + 1) == '/')) {
                this.i++;
            }
            this.i += 2;
        }
    }

    private String word() throws Exception {
//        start = self.i
//        while self.i < len(self.s):
//        if self.s[self.i].isalnum() or self.s[self.i] in "#-.%":
//        self.i += 1
//        else:
//        break
//        if not (self.i > start):
//        raise Exception("Parsing error")
//        return self.s[start:self.i]

        this.whitespace();
        Integer start = this.i;
        while (this.i < this.s.length()) {
//            System.out.println(this.s.charAt(this.i));
            if (Character.isLetterOrDigit(this.s.charAt(this.i)) || "-_.#%".contains("" +this.s.charAt(this.i))) {
                this.i += 1;
            } else {break;}
        }

        if (!(this.i>start)) {
            System.out.println("PARSING ERROR IN CSS WORD at index " + this.i);
            System.out.println(this.s.substring(Math.max(this.i-50,0), this.i) + "####" + this.s.substring(this.i, Math.min(this.i+100, this.s.length())));
//            System.out.println(this.s.substring(this.i));
            throw new Exception("Parsing error in CSS Word");
        }
        return this.s.substring(start, this.i);
    }

    private void literal(char literal) throws Exception {
//        System.out.println("LITERAL " + this.s.charAt(this.i) + literal);
        if (!(this.i < this.s.length() && this.s.charAt(this.i) == literal)) {
            System.out.println("PARSING ERROR IN CSS LITERAL. Expected '" + literal + "' but got '" +
                    this.s.charAt(this.i) + "'.i=" + this.i);
            System.out.println(this.s.substring(Math.max(this.i-50,0), this.i) + "####" + this.s.substring(this.i, Math.min(this.i+100, this.s.length())));
            throw new Exception("Parsing error in CSS Literal");
        }
        this.i += 1;
    }

    private Pair<String, String> pair() throws Exception {
        Pair<String, String> pRet = new Pair<>();
        String tw = this.word().toLowerCase();
//        System.out.println("PAIR WORD " + tw);
        pRet.setFirst(tw);
        this.whitespace();
        this.literal(':');
        this.whitespace();
        int start = this.i;
        while (this.i < this.s.length()
                && this.s.charAt(this.i) != ';'
                && this.s.charAt(this.i) != '}') {
            this.i++;
        }
        pRet.setSecond(this.s.substring(start, this.i).trim());
        return pRet;
    }

    private ArrayList<Pair<String, String>> expandPair(Pair<String, String> p) {
        ArrayList<Pair<String, String>> al = new ArrayList<>();
        ArrayList<String> outlineStyles = new ArrayList<>(){{
            add("auto");
            add("none");
            add("dotted");
            add("dashed");
            add("solid");
            add("double");
            add("groove");
            add("ridge");
            add("inset");
            add("outset");
        }};

        if (p.getFirst().equals("outline") || p.getFirst().equals("border-bottom")
            || p.getFirst().equals("border-top") || p.getFirst().equals("border-left") || p.getFirst().equals("border-right")) {
            // need to split into width, style, color
            // the only thing with a number is the width
            // style could be any of the below
            // the remaining thing is the color
            String[] ospl = p.getSecond().split(" ");
            if (ospl.length != 3) return al; // NOTE: silently fails if something is wrong
            // we know length is 3

            String color = "", style = "", width = "";
            for (String op : ospl) {
                if (op.matches("\\d+.*")) {
                    // number then some text
                    width = op;
                } else if (outlineStyles.contains(op)) {
                    // style
                    style = op;
                } else {
                    color = op;
                }
            }
            al.add(new Pair<>(p.getFirst()+"-width", width));
            al.add(new Pair<>(p.getFirst()+"-style", style));
            al.add(new Pair<>(p.getFirst()+"-color", color));
        } else if ( p.getFirst().equals("border")) {
            String[] ospl = p.getSecond().split(" ");
            if (ospl.length != 3) return al; // NOTE: silently fails if something is wrong
            // we know length is 3

            String color = "", style = "", width = "";
            for (String op : ospl) {
                if (op.matches("\\d+.*")) {
                    // number then some text
                    width = op;
                } else if (outlineStyles.contains(op)) {
                    // style
                    style = op;
                } else {
                    color = op;
                }
            }
            ArrayList<String> sides = new ArrayList<>() {{add("bottom"); add("top"); add("left"); add("right");}};
            for (String side : sides) {
                al.add(new Pair<>("border-"+side+"-width", width));
                al.add(new Pair<>("border-"+side+"-style", style));
                al.add(new Pair<>("border-"+side+"-color", color));
            }
        } else if (p.getFirst().equals("margin") || p.getFirst().equals("padding") || p.getFirst().equals("inset")) {
            // TODO: margin auto for L/R centres horizontally - idk where best to implement this
            // margin/padding/inset can have 1, 2, 3, or 4 values
            // if 1: set all sides to the value
            // if 2: first is T/B, second is L/R
            // if 3: T, L/R, B
            // if 4: T, R, B, L
            // order does matter
            String[] spl = p.getSecond().split(" ");
            if (spl.length == 1) {
                al.add(new Pair<>(p.getFirst()+"-top", spl[0]));
                al.add(new Pair<>(p.getFirst()+"-bottom", spl[0]));
                al.add(new Pair<>(p.getFirst()+"-left", spl[0]));
                al.add(new Pair<>(p.getFirst()+"-right", spl[0]));
            } else if (spl.length == 2) {
                al.add(new Pair<>(p.getFirst()+"-top", spl[0]));
                al.add(new Pair<>(p.getFirst()+"-bottom", spl[0]));
                al.add(new Pair<>(p.getFirst()+"-left", spl[1]));
                al.add(new Pair<>(p.getFirst()+"-right", spl[1]));
            } else if (spl.length == 3) {
                al.add(new Pair<>(p.getFirst()+"-top", spl[0]));
                al.add(new Pair<>(p.getFirst()+"-bottom", spl[2]));
                al.add(new Pair<>(p.getFirst()+"-left", spl[1]));
                al.add(new Pair<>(p.getFirst()+"-right", spl[1]));
            } else if (spl.length == 4) {
                al.add(new Pair<>(p.getFirst()+"-top", spl[0]));
                al.add(new Pair<>(p.getFirst()+"-bottom", spl[2]));
                al.add(new Pair<>(p.getFirst()+"-left", spl[3]));
                al.add(new Pair<>(p.getFirst()+"-right", spl[1]));
            } else return al;

        } else if (p.getFirst().equals("border-width") || p.getFirst().equals("border-style") || p.getFirst().equals("border-color")) {
            // these are the same as above, but instead of border-width-left it is border-left-width
            String[] spl = p.getSecond().split(" ");
            String[] ps = p.getFirst().split("-");
            String o = ps[0];
            String t = ps[1];
            if (spl.length == 1) {
                al.add(new Pair<>(o+"-top-"+t, spl[0]));
                al.add(new Pair<>(o+"-bottom-"+t, spl[0]));
                al.add(new Pair<>(o+"-left-"+t, spl[0]));
                al.add(new Pair<>(o+"-right-"+t, spl[0]));
            } else if (spl.length == 2) {
                al.add(new Pair<>(o+"-top-"+t, spl[0]));
                al.add(new Pair<>(o+"-bottom-"+t, spl[0]));
                al.add(new Pair<>(o+"-left-"+t, spl[1]));
                al.add(new Pair<>(o+"-right-"+t, spl[1]));
            } else if (spl.length == 3) {
                al.add(new Pair<>(o+"-top-"+t, spl[0]));
                al.add(new Pair<>(o+"-bottom-"+t, spl[2]));
                al.add(new Pair<>(o+"-left-"+t, spl[1]));
                al.add(new Pair<>(o+"-right-"+t, spl[1]));
            } else if (spl.length == 4) {
                al.add(new Pair<>(o+"-top-"+t, spl[0]));
                al.add(new Pair<>(o+"-bottom-"+t, spl[2]));
                al.add(new Pair<>(o+"-left-"+t, spl[3]));
                al.add(new Pair<>(o+"-right-"+t, spl[1]));
            } else return al;
        } else if (p.getFirst().equals("border-radius")) {
            // top left, top right, bottom right, bottom left
            String[] spl = p.getSecond().split(" ");
            if (spl.length == 1) {
                al.add(new Pair<>("border-top-left-radius", spl[0]));
                al.add(new Pair<>("border-top-right-radius", spl[0]));
                al.add(new Pair<>("border-bottom-right-radius", spl[0]));
                al.add(new Pair<>("border-bottom-left-radius", spl[0]));
            } else if (spl.length == 2) {
                al.add(new Pair<>("border-top-left-radius", spl[0]));
                al.add(new Pair<>("border-top-right-radius", spl[1]));
                al.add(new Pair<>("border-bottom-right-radius", spl[0]));
                al.add(new Pair<>("border-bottom-left-radius", spl[1]));
            } else if (spl.length == 3) {
                al.add(new Pair<>("border-top-left-radius", spl[0]));
                al.add(new Pair<>("border-top-right-radius", spl[1]));
                al.add(new Pair<>("border-bottom-right-radius", spl[2]));
                al.add(new Pair<>("border-bottom-left-radius", spl[1]));
            } else if (spl.length == 4) {
                al.add(new Pair<>("border-top-left-radius", spl[0]));
                al.add(new Pair<>("border-top-right-radius", spl[2]));
                al.add(new Pair<>("border-bottom-right-radius", spl[3]));
                al.add(new Pair<>("border-bottom-left-radius", spl[1]));
            } else return al;
        } else {
            // if it gets to here the pair doesn't need splitting so return it
            al.add(p);
        }
        return al;
    }

    protected HashMap<String, String> body() {
        this.whitespace();
        if (this.i < this.s.length() && this.s.charAt(this.i) == '}') {
            return new HashMap<>();
        }
        HashMap<String, String> hRet = new HashMap<>();
        while (this.i < this.s.length() && this.s.charAt(this.i) != '}') {
            try {
                Pair<String, String> _p = this.pair();
                ArrayList<Pair<String, String>> al = this.expandPair(_p);
                for (Pair<String, String> p : al) {
                    hRet.put(p.getFirst(), p.getSecond());
                }
//                hRet.put(_p.getFirst(), _p.getSecond());
                this.whitespace();
                if (this.i == this.s.length()) {
                    break;
                }
                if (this.s.charAt(this.i) == '}') {} else {
                    this.literal(';');
                }
                this.whitespace();
            } catch (Exception e) {
//                System.out.println("ISSUE in BODY" + e);
                Character why = this.ignoreUntil(new ArrayList<>(){{add(';');add('}');}});
                if (why != null && why == ';') {
                    try {
                        this.literal(';');
                    } catch (Exception ex) {/* this will never error but to keep compiler happy*/}
                    this.whitespace();
                } else {break;}
            }
        }
        return hRet;
    }

    private ISelector selector() {
        ISelector out = new TagSelector("-ICOSAERR");
        try {
//            if (this.i == 3317) System.out.println("WORDSHIT1");
            out = new TagSelector(this.word().toLowerCase());
            this.whitespace();
            while (this.i < this.s.length() && this.s.charAt(this.i) != '{' && this.s.charAt(this.i) != ',') {
                // TODO: not totally sure if this list of checks should be else or just plain ifs with checks for { between
                if (this.s.charAt(this.i) == ':') {
                    ignoreUntil(new ArrayList<>() {{
                        add('{');
                    }});
                    // line below to be removed when support added
                    out = new TagSelector("-ICOSAERR");
                    // todo: add support for the ::before etc tags
//                    System.out.println("ignored to { because of :. i = " + this.i);
                } else if (this.s.charAt(this.i) == '~') {
                    this.literal('~');
                    String tag = this.word();
                    ISelector sib2 = new TagSelector(tag.toLowerCase());
                    out = new PreviousSiblingSelector(out, sib2);
                } else if (this.s.charAt(this.i) == '*') {
                    this.literal('*');
                    out = new AllDescendantSelector(out);
                } else if (this.s.charAt(this.i) == '+') {
                    this.literal('+');
                    String tag = this.word();
                    ISelector sib2 = new TagSelector(tag.toLowerCase());
                    out = new AdjacentSiblingSelector(out, sib2);
                } else if (this.s.charAt(this.i) == '>') {
                    this.literal('>');
                    String tag = this.word();
                    ISelector descendant = new TagSelector(tag.toLowerCase());
                    out = new DirectDescendantSelector(out, descendant);
                } else if (this.s.charAt(this.i) == '[') {
                    ignoreUntil(new ArrayList<>(){{ add(']'); }});
                    this.literal(']');
                    // ignore this for the moment.
                    // TODO: implement (attribute selector)
                } else {
                    String tag = this.word();
                    ISelector descendant = new TagSelector(tag.toLowerCase());
                    out = new DescendantSelector(out, descendant);
                }
                this.whitespace();
            }
            if (this.i < this.s.length() && (this.s.charAt(this.i) == ',')) {
                ignoreUntil(new ArrayList<>(){{ add('{'); }});
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return out;
    }

    public ArrayList<Pair<ISelector, HashMap<String, String>>> parse() {
        ArrayList<Pair<ISelector, HashMap<String, String>>> rules = new ArrayList<>();
        while (this.i < this.s.length()) {
            try {
                this.whitespace();
                if (this.s.charAt(this.i) == '@') {
                    // make this handle nested selectors in the media
                    ignoreUntil(new ArrayList<>(){{ add('{'); }});
                    this.literal('{');
                    int toClose = 1;
                    while (toClose > 0) {
                        ignoreUntil(new ArrayList<>(){{ add('}'); add('{'); }});
                        if (this.s.charAt(this.i) == '{') {
                            toClose++;
                            this.literal('{');
                        } else {
                            toClose--;
                            this.literal('}');
                        }
                        this.whitespace();
                    }

                    continue;
                }
                ISelector sl = this.selector();
                this.whitespace();
                this.literal('{');
                this.whitespace();
                HashMap<String, String> bdy = this.body();
                this.literal('}');
                rules.add(new Pair<>(sl, bdy));
                this.whitespace();
            } catch (Exception e) {
                System.out.println("B" + e);
                Character why = this.ignoreUntil(new ArrayList<>(){{add('}');}});
                if (why != null && why == ';') {
                    try {
                        this.literal(';');
                    } catch (Exception ex) {/* this will never error but to keep compiler happy*/}
                    this.whitespace();
                } else {break;}
            }
        }
//        System.out.println(rules);
        return rules;
    }


    private Character ignoreUntil(ArrayList<Character> chars) {
        while (this.i < this.s.length()) {
            if (chars.contains(this.s.charAt(this.i))) {
                char c = this.s.charAt(this.i);
//                this.i++;
                return c;
            } else {
                this.i += 1;
            }
        }
        return null;
    }

}
