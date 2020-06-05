package com.example.serveIt.helper_classes;


import com.example.serveIt.helper_classes.Food_Item;

import java.io.Serializable;

public class Order_Item implements Serializable {
    private Food_Item item;
    private int quantity;
    private String notes;
    private double price;


    public Order_Item(Food_Item item, int quantity, String notes) {
        this.item = item;
        this.quantity = quantity;
        this.notes = notes;
        this.price = Double.parseDouble(item.getPrice());

    }

    public Order_Item(){

    }

    public Food_Item getItem() {
        return item;
    }

    public void setItem(Food_Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incQuantity() {
        this.quantity++;
    }

    public void decQuantity() {this.quantity--;}

    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


}