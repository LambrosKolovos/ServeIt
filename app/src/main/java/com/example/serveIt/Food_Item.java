package com.example.serveIt;

import java.io.Serializable;

public class Food_Item implements Serializable {
    private String name;
    private String price;
    private int quantity = 1;
    private boolean addItem;

    public Food_Item(String name, String price) {
        this.name = name;
        double tempPrice = Double.parseDouble(price);
        this.price = tempPrice + "$";
        this.addItem = false;
    }

    public Food_Item(){

    }

    public Food_Item(String name, String price, boolean addItem){
        this.addItem = addItem;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public double getPriceOrder() {
        if (price.length() == 4) {
            price = price.substring(0, price.length() - 1);
        }
        return Double.parseDouble(price);
    }

    public void incQuanity() {
        this.quantity++;
    }

    public int getQuantity() {
        return quantity;
    }
}
