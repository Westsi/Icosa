package com.github.westsi.vsi;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Engine {

    public static void RunEngine() {
        Engine vsi = Engine.getEngine();
        System.out.println(vsi);
        HashMap<Integer, String> documents  = new HashMap<>();
        documents.put(0, "At Scale You Will Hit Every Performance Issue I used to think I knew a bit about performance scalability and how to keep things trucking when you hit large amounts of data Truth is I know diddly squat on the subject since the most I have ever done is read about how its done To understand how I came about realising this you need some background");
        documents.put(1, "Richard Stallman to visit Australia Im not usually one to promote events and the like unless I feel there is a genuine benefit to be had by attending but this is one stands out Richard M Stallman the guru of Free Software is coming Down Under to hold a talk You can read about him here Open Source Celebrity to visit Australia");
        documents.put(2, "MySQL Backups Done Easily One thing that comes up a lot on sites like Stackoverflow and the like is how to backup MySQL databases The first answer is usually use mysqldump This is all fine and good till you start to want to dump multiple databases You can do this all in one like using the all databases option however this makes restoring a single database an issue since you have to parse out the parts you want which can be a pain");
        documents.put(3, "Why You Shouldnt roll your own CAPTCHA At a TechEd I attended a few years ago I was watching a presentation about Security presented by Rocky Heckman read his blog its quite good In it he was talking about security algorithms The part that really stuck with me went like this");
        documents.put(4, "The Great Benefit of Test Driven Development Nobody Talks About The feeling of productivity because you are writing lots of code Think about that for a moment Ask any developer who wants to develop why they became a developer One of the first things that comes up is I enjoy writing code This is one of the things that I personally enjoy doing Writing code any code especially when its solving my current problem makes me feel productive It makes me feel like Im getting somewhere Its empowering");
        documents.put(5, "Setting up GIT to use a Subversion SVN style workflow Moving from Subversion SVN to GIT can be a little confusing at first I think the biggest thing I noticed was that GIT doesnt have a specific workflow you have to pick your own Personally I wanted to stick to my Subversion like work-flow with a central server which all my machines would pull and push too Since it took a while to set up I thought I would throw up a blog post on how to do it");
        documents.put(6, "Why CAPTCHA Never Use Numbers 0 1 5 7 Interestingly this sort of question pops up a lot in my referring search term stats Why CAPTCHAs never use the numbers 0 1 5 7 Its a relativity simple question with a reasonably simple answer Its because each of the above numbers are easy to confuse with a letter See the below");

        HashMap<Integer, HashMap<String, Integer>> index = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            index.put(i, vsi.Concordance(documents.get(i).toLowerCase()));
        }

        Scanner userInput = new Scanner(System.in);
        System.out.println("What are you looking for?");
        String searchTerm = userInput.nextLine();

        HashMap<Double, String> matches = new HashMap<>();

        for (int i = 0; i < index.size(); i++) {
            double relation = vsi.Relation(vsi.Concordance(searchTerm.toLowerCase()), index.get(i));
            if (relation != 0) {
                matches.put(relation, documents.get(i).substring(0, 100));
            }
        }

        for (Double key : matches.keySet()) {
            System.out.println(key.toString() + " " + matches.get(key));
        }
    }
    public static Engine getEngine() {
        if (engine == null) {
            engine = new Engine();
        }
        return engine;
    }
    private Engine() {}
    private static Engine engine;

    public HashMap<String, Integer> Concordance(String document) {
        HashMap<String, Integer> map = new HashMap<>();
        String[] words = document.trim().split("\\s+");
        for (String word: words) {
            map.put(word, map.getOrDefault(word, 1));
        }
        return map;
    }

    public double Magnitude(HashMap<String, Integer> concordance) {
        double total = 0;
        for (Map.Entry<String, Integer> entry : concordance.entrySet()) {
            total += Math.pow((double) entry.getValue(), 2);
        }
        return Math.sqrt(total);
    }

    public double Relation(HashMap<String, Integer> conc1, HashMap<String, Integer> conc2) {
        double relevance = 0;
        double topval = 0;
        for (Map.Entry<String, Integer> entry : conc1.entrySet()) {
            if (conc2.containsKey(entry.getKey())) {
                topval += entry.getValue() * conc2.get(entry.getKey());
            }
        }
        if (Magnitude(conc1) * Magnitude(conc2) != 0) {
            return topval / Magnitude(conc1) * Magnitude(conc2);
        }
        return 0;
    }

}
