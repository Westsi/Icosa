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
//        browser.LoadWebPage("https://browser.engineering/text.html");
//        browser.LoadWebPage("https://hweeo");
        browser.LoadWebPage("data:text/html,<b>Hi</b>");
    }
}