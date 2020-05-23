package com.example.serveIt;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private ArrayList<Order_Item> ordered;
    private double total_price;

    public Order(){
        ordered = new ArrayList<>();
    }
    public ArrayList<Order_Item> getOrdered() {
        return ordered;
    }

    public void addItem(Order_Item x){
        ordered.add(x);
    }

    public void setOrdered(ArrayList<Order_Item> ordered) {
        this.ordered = ordered;
    }

}
