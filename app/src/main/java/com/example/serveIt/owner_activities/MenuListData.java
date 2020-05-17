package com.example.serveIt.owner_activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuListData {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> categories = new HashMap<String, List<String>>();

        List<String> drinks = new ArrayList<String>();
        drinks.add("WINE");
        drinks.add("BEER");
        drinks.add("WATER 1,5L");
        drinks.add("ADD NEW");

        List<String> burgers = new ArrayList<String>();
        burgers.add("BURGER 1");
        burgers.add("BURGER 2");
        burgers.add("BURGER 3");
        burgers.add("BURGER 5");
        burgers.add("ADD NEW");

        List<String> salads = new ArrayList<String>();
        salads.add("SALAD 1");
        salads.add("SALAD 2");
        salads.add("SALAD 3");
        salads.add("ADD NEW");

        categories.put("CATEGORY 1", drinks);
        categories.put("CATEGORY 2", burgers);
        categories.put("CATEGORY 3", salads);
        return categories;
    }
}
