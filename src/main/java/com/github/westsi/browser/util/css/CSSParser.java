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
        while (this.i < this.s.length() && Character.isWhitespace(this.s.charAt(this.i))) {
            this.i += 1;
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
            if (Character.isLetterOrDigit(this.s.charAt(this.i)) || "#-.%".contains("" +this.s.charAt(this.i))) {
                this.i += 1;
            } else {break;}
        }

        if (!(this.i>start)) {
            System.out.println("PARSING ERROR IN CSS WORD");
//            System.out.println(this.s.substring(this.i));
            throw new Exception("Parsing error in CSS Word");
        }
        return this.s.substring(start, this.i);
    }

    private void literal(char literal) throws Exception {
//        System.out.println("LITERAL " + this.s.charAt(this.i) + literal);
        if (!(this.i < this.s.length() && this.s.charAt(this.i) == literal)) {
            System.out.println("PARSING ERROR IN CSS LITERAL");
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
        pRet.setSecond(this.word());
        return pRet;
    }

    protected HashMap<String, String> body() {
        HashMap<String, String> hRet = new HashMap<>();
        while (this.i < this.s.length() && this.s.charAt(this.i) != '}') {
            try {
                Pair<String, String> p = this.pair();
                hRet.put(p.getFirst(), p.getSecond());
                this.whitespace();
                if (this.i == this.s.length()) {
                    break;
                }
                this.literal(';');
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
            out = new TagSelector(this.word().toLowerCase());
            this.whitespace();
            while (this.i < this.s.length() && this.s.charAt(this.i) != '{') {
                String tag = this.word();
                ISelector descendant = new TagSelector(tag.toLowerCase());
                out = new DescendantSelector(out, descendant);
                this.whitespace();
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
                ISelector sl = this.selector();
                this.literal('{');
                this.whitespace();
                HashMap<String, String> bdy = this.body();
                this.literal('}');
                rules.add(new Pair<>(sl, bdy));
            } catch (Exception e) {
                System.out.println(e);
                Character why = this.ignoreUntil(new ArrayList<>(){{add('}');}});
                if (why != null && why == ';') {
                    try {
                        this.literal(';');
                    } catch (Exception ex) {/* this will never error but to keep compiler happy*/}
                    this.whitespace();
                } else {break;}
            }
        }
        System.out.println(rules);
        return rules;
    }


    private Character ignoreUntil(ArrayList<Character> chars) {
        while (this.i < this.s.length()) {
            if (chars.contains(this.s.charAt(this.i))) {
                return this.s.charAt(this.i);
            } else {
                this.i += 1;
            }
        }
        return null;
    }

}
