package com.example.serveIt;

public class Food_Item {
    private String name;
    private double price;

    public Food_Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Food_Item(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
