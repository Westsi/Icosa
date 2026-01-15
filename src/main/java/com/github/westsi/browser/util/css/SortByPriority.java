package com.github.westsi.browser.util.css;

import com.github.westsi.browser.util.Pair;

import java.util.Comparator;
import java.util.HashMap;

public class SortByPriority implements Comparator<Pair<ISelector, HashMap<String, String>>> {
    @Override
    public int compare(Pair<ISelector, HashMap<String, String>> o1, Pair<ISelector, HashMap<String, String>> o2) {
        return o1.getFirst().getPriority() - o2.getFirst().getPriority(); // i think this is the right order but check if bugs
    }
}
