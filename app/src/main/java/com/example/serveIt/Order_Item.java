package com.example.serveIt;


import java.io.Serializable;

public class Order_Item implements Serializable {
    private Food_Item item;
    private int quantity;
    private String notes;


    public Order_Item(Food_Item item, int quantity, String notes) {
        this.item = item;
        this.quantity = quantity;
        this.notes = notes;
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