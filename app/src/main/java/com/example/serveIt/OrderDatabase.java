package com.example.serveIt;

import java.util.ArrayList;

public class OrderDatabase {

    private ArrayList<Order_Item> orderItems;
    private boolean isReady;
    private String table;

    public OrderDatabase(ArrayList<Order_Item> order_items, String table){
        this.orderItems = order_items;
        this.table = table;
        isReady = false;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public ArrayList<Order_Item> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<Order_Item> orderItems) {
        this.orderItems = orderItems;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
