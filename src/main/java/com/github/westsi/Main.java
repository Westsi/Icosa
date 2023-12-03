package com.github.westsi;

import com.github.westsi.browser.Browser;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Icosa!");
        System.out.println("Xonize Inc.");
        Browser browser = Browser.getInstance();
        Scanner userInput = new Scanner(System.in);
        System.out.println("Enter URL: ");
        String url = userInput.nextLine();
        browser.LoadWebPage(url);
    }
}