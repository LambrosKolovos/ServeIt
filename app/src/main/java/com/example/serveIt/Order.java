package com.example.serveIt;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Order implements Serializable {
    private ArrayList<Food_Item> ordered;
    private double total_price;

    public Order(){
        ordered = new ArrayList<>();
    }

    public ArrayList<Food_Item> getOrdered() {
        return ordered;
    }

    public void addItem(Food_Item x){
        ordered.add(x);
    }

    public Food_Item getItem(Food_Item wanted){
        Food_Item toReturn = new Food_Item();
        for(Food_Item x: ordered){
            if(wanted.getName().equals(x.getName()))
                toReturn = x;
        }
        return toReturn;
    }

    public boolean containsItem(Food_Item wanted){
        for(Food_Item x: ordered){
            if(wanted.getName().equals(x.getName()))
               return true;
        }
        return false;
    }

    public void removeItem(String removeItem){
        for (Iterator<Food_Item> iterator = ordered.iterator(); iterator.hasNext(); ) {
            Food_Item value = iterator.next();
            if (value.getName().equals(removeItem)) {
                iterator.remove();
            }
        }
        calculatePrice();
    }

    private void calculatePrice(){
        total_price = 0;
        for(Food_Item x: ordered)
            total_price += x.getPriceOrder() * x.getQuantity();
    }

    public double getTotal_price() {
        calculatePrice();
        return total_price;
    }


}
