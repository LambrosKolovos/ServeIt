package com.example.serveIt;

import java.util.ArrayList;

public class Store {
    private ArrayList<User> employees;
    private ArrayList<Category> menu;
    private ArrayList<Table> tables;
    private String name;
    private String ownerID;

    public Store(String name, String ownerID) {
        this.name = name;
        this.ownerID = ownerID;
    }

    public ArrayList<User> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<User> employees) {
        this.employees = employees;
    }

    public ArrayList<Category> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<Category> menu) {
        this.menu = menu;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
}
