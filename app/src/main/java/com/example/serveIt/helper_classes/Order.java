package com.example.serveIt.helper_classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

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

    public Order_Item getItem(Food_Item wanted){
        Order_Item toReturn = new Order_Item();
        for(Order_Item x: ordered){
            if(wanted.getName().equals(x.getItem().getName()))
                toReturn = x;
        }
        return toReturn;
    }

    public boolean containsItem(Food_Item wanted){
        for(Order_Item x: ordered){
            if(wanted.getName().equals(x.getItem().getName()))
               return true;
        }
        return false;
    }


    public void removeItem(String removeItem){
        for (Iterator<Order_Item> iterator = ordered.iterator(); iterator.hasNext(); ) {
            Order_Item value = iterator.next();
            if (value.getItem().getName().equals(removeItem)) {
                iterator.remove();
            }
        }
        calculatePrice();
    }

    public void removeAll(){
        ordered.clear();
    }

    private void calculatePrice(){
        total_price = 0;
        for(Order_Item x: ordered)
            total_price += x.getPrice() * x.getQuantity();
    }

    public double getTotal_price() {
        calculatePrice();
        return total_price;
    }

    public Order_Item findItem(String text){
        for (Iterator<Order_Item> iterator = ordered.iterator(); iterator.hasNext(); ) {
            Order_Item value = iterator.next();
            if (value.getItem().getName().equals(text)) {
               return value;
            }
        }
        return null;
    }



}
