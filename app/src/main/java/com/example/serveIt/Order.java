package com.example.serveIt;

import java.util.ArrayList;

public class Order {
    private ArrayList<Order_Item> ordered;
    private double total_price;
    private int table_id;

    public Order(ArrayList<Order_Item> ordered, double total_price, int table_id) {
        this.ordered = ordered;
        this.total_price = total_price;
        this.table_id = table_id;
    }

    public ArrayList<Order_Item> getOrdered() {
        return ordered;
    }

    public void setOrdered(ArrayList<Order_Item> ordered) {
        this.ordered = ordered;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }
}
