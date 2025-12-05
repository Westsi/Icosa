package com.github.westsi;

import com.github.westsi.browser.Browser;
import com.github.westsi.browser.BrowserTab;

import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Icosa!");
        System.out.println("Xonize Inc.");
//        Scanner userInput = new Scanner(System.in);
//        System.out.println("Enter URL: ");
//        String url = userInput.nextLine();
        Browser browser = Browser.getInstance();
//        browser.LoadWebPage(url);
//        browser.LoadWebPage("https://gist.githubusercontent.com/Westsi/58b5aeeb2c53e6682bed13eacdb7be8c/raw/6317dacce0eb91f924e1d0ea43bf68ace889f89b/test.html");
//        browser.LoadWebPage("https://browser.engineering/examples/example3-sizes.html");
        browser.LoadWebPage("https://browser.engineering/layout.html");
//        browser.LoadWebPage("https://hweeo");
//        browser.LoadWebPage("data:text/html,<b>Hi&lt;&gt;</b>");
//        browser.LoadWebPage(("data:text/html,<h1>Hello World!</h1><pre class=\"sourcecode python\"><p>Icosa</p></pre>"));
        browser.LoadWebPage("data:text/html,<p>Let’s start by looking at how the existing <code>Layout</code> class\n" +
                "is used:</p>\n" +
                "<div class=\"sourceCode\" id=\"cb1\" data-file=\"lab4\"><pre\n" +
                "class=\"sourceCode python\"><code class=\"sourceCode python\"><span id=\"cb1-1\"><a href=\"#cb1-1\" aria-hidden=\"true\" tabindex=\"-1\"></a><span class=\"kw\">class</span> Browser:</span>\n" +
                "<span id=\"cb1-2\"><a href=\"#cb1-2\" aria-hidden=\"true\" tabindex=\"-1\"></a>    <span class=\"kw\">def</span> load(<span class=\"va\">self</span>, url):</span>\n" +
                "<span id=\"cb1-3\"><a href=\"#cb1-3\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"co\"># ...</span></span>\n" +
                "<span id=\"cb1-4\"><a href=\"#cb1-4\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"va\">self</span>.display_list <span class=\"op\">=</span> Layout(<span class=\"va\">self</span>.nodes).display_list</span>\n" +
                "<span id=\"cb1-5\"><a href=\"#cb1-5\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"co\">#...</span></span></code></pre></div>\n" +
                "<p>Here, a <code>Layout</code> object is created briefly and then thrown\n" +
                "away. Let’s instead make it the beginning of our layout tree by storing\n" +
                "it in a <code>Browser</code> field:</p>\n" +
                "<div class=\"sourceCode\" id=\"cb2\" data-replace=\"Layout/DocumentLayout\"\n" +
                "data-dropline=\"display_list\"><pre class=\"sourceCode python\"><code class=\"sourceCode python\"><span id=\"cb2-1\"><a href=\"#cb2-1\" aria-hidden=\"true\" tabindex=\"-1\"></a><span class=\"kw\">class</span> Browser:</span>\n" +
                "<span id=\"cb2-2\"><a href=\"#cb2-2\" aria-hidden=\"true\" tabindex=\"-1\"></a>    <span class=\"kw\">def</span> load(<span class=\"va\">self</span>, url):</span>\n" +
                "<span id=\"cb2-3\"><a href=\"#cb2-3\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"co\"># ...</span></span>\n" +
                "<span id=\"cb2-4\"><a href=\"#cb2-4\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"va\">self</span>.document <span class=\"op\">=</span> Layout(<span class=\"va\">self</span>.nodes)</span>\n" +
                "<span id=\"cb2-5\"><a href=\"#cb2-5\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"va\">self</span>.document.layout()</span>\n" +
                "<span id=\"cb2-6\"><a href=\"#cb2-6\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"va\">self</span>.display_list <span class=\"op\">=</span> <span class=\"va\">self</span>.document.display_list</span>\n" +
                "<span id=\"cb2-7\"><a href=\"#cb2-7\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"co\">#...</span></span></code></pre></div>\n" +
                "<p>Note that I’ve renamed the <code>Layout</code> constructor to a\n" +
                "<code>layout</code> method, so that constructing a layout object and\n" +
                "actually laying it out can be different steps. The constructor now just\n" +
                "stores the node it was passed:</p>\n" +
                "<div class=\"sourceCode\" id=\"cb3\" data-replace=\"Layout/BlockLayout\"><pre\n" +
                "class=\"sourceCode python\"><code class=\"sourceCode python\"><span id=\"cb3-1\"><a href=\"#cb3-1\" aria-hidden=\"true\" tabindex=\"-1\"></a><span class=\"kw\">class</span> Layout:</span>\n" +
                "<span id=\"cb3-2\"><a href=\"#cb3-2\" aria-hidden=\"true\" tabindex=\"-1\"></a>    <span class=\"kw\">def</span> <span class=\"fu\">__init__</span>(<span class=\"va\">self</span>, node):</span>\n" +
                "<span id=\"cb3-3\"><a href=\"#cb3-3\" aria-hidden=\"true\" tabindex=\"-1\"></a>        <span class=\"va\">self</span>.node <span class=\"op\">=</span> node</span></code></pre></div>\n" +
                "<p>So far, we still don’t have a tree—we just have a single\n" +
                "<code>Layout</code> object. To make it into a tree, we’ll need to add\n" +
                "child and parent pointers. I’m also going to add a pointer to the\n" +
                "previous sibling, because that’ll be useful for computing sizes and\n" +
                "positions later:</p>");
    }
}