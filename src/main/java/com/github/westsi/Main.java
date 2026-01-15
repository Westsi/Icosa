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
        browser.LoadWebPage("https://browser.engineering/styles.html");
//        browser.LoadWebPage("https://youtube.com");
//        browser.LoadWebPage("https://westsi.pages.dev");
//        browser.LoadWebPage("https://hweeo");
//          browser.LoadWebPage("data:text/html,<div class=\"sourceCode\" id=\"cb1\"><pre\n" +
//                  "class=\"sourceCode html example\"><code class=\"sourceCode\"><span id=\"cb1-1\"><a href=\"#cb1-1\" aria-hidden=\"true\" tabindex=\"-1\"></a><span class=\"dt\">&lt;</span><span class=\"kw\">div</span><span class=\"ot\"> style</span><span class=\"op\">=</span><span class=\"st\">&quot;background-color:lightblue&quot;</span><span class=\"dt\">&gt;</span>Blue background<span class=\"dt\">&lt;/</span><span class=\"kw\">div</span><span class=\"dt\">&gt;</span></span></code></pre></div>\n" +
//                  "<p>And it renders like this:</p>\n" +
//                  "<div style=\"background-color:lightblue\">\n" +
//                  "Blue background\n" +
//                  "</div>");
//        browser.LoadWebPage("data:text/html,<div><i>Hello, </i><b>world!</b><p>So it began&lt;&gt;</p> <b>This</b> <em>seems</em> to <big>WORK</big></div>");
//        browser.LoadWebPage(("data:text/html,<h1>Hello World!</h1><p>a</p><p>a</p><p>a <i>b</i> <b>c</b> d</p><p>a</p><p>a</p><p>a</p><p>a</p><p>a</p><p>a</p><pre class=\"sourcecode python\"><p>Icosa</p></pre>"));
    }
}