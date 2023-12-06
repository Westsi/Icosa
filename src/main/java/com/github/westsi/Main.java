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
//        browser.LoadWebPage("https://browser.engineering/examples/xiyouji.html");
        browser.LoadWebPage("https://gist.githubusercontent.com/Westsi/58b5aeeb2c53e6682bed13eacdb7be8c/raw/734282d245299b0e398146b26f3e84acec10988a/test.html");
//        browser.LoadWebPage("https://hweeo");
    }
}