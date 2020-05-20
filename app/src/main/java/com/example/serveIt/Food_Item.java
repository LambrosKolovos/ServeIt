package com.example.serveIt;

public class Food_Item {
    private String name;
    private String price;
    private boolean addItem;

    public Food_Item(String name, String price) {
        this.name = name;
        double tempPrice = Double.parseDouble(price);
        this.price = String.valueOf(tempPrice);
        this.addItem = false;
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
        if(addItem)
            return price;
        else
            return price + "$";
    }

}
