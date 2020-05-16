package com.example.serveIt;

import java.util.ArrayList;

public class Category {
    private ArrayList<Food_Item> list;
    private String name;

    public Category(ArrayList<Food_Item> list, String name) {
        this.list = list;
        this.name = name;
    }

    public ArrayList<Food_Item> getList() {
        return list;
    }

    public void setList(ArrayList<Food_Item> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
