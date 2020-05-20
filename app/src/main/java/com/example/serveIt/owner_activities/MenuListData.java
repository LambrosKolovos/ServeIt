package com.example.serveIt.owner_activities;

import com.example.serveIt.Food_Item;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MenuListData {

    private static LinkedHashMap<String, List<Food_Item>> categories = new LinkedHashMap<>();

    public static LinkedHashMap<String, List<Food_Item>> getData() {
        return categories;
    }

    public static void addCategory(String name){
        List<Food_Item> newCategory = new ArrayList<>();
        Food_Item addNew = new Food_Item("Add new", "+", true);
        newCategory.add(addNew);
        categories.put(name, newCategory);
    }

    public static void addItem(Food_Item item, String category){
        for (LinkedHashMap.Entry<String, List<Food_Item>> entry : categories.entrySet()) {
            String categoryName = entry.getKey();
            List<Food_Item> categoryList = entry.getValue();

            if(categoryName.equals(category))
                categoryList.add(0, item);
        }
    }

    public static void removeItem(Food_Item item, String category){
        for (LinkedHashMap.Entry<String, List<Food_Item>> entry : categories.entrySet()) {
            String categoryName = entry.getKey();
            List<Food_Item> categoryList = entry.getValue();

            if(categoryName.equals(category))
                categoryList.remove(item);
        }
    }


}
