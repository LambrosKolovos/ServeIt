package com.example.serveIt.helper_classes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String email;
    private String full_name;
    private boolean isOwner;
    private String storeID;
    private String workID;
    private String date;

    public User(String email, String full_name, boolean isOwner){
        this.email = email;
        this.full_name = full_name;
        this.isOwner = isOwner;
    }

    public User(){

    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getWorkID() {
        return workID;
    }

    public void setWorkID(String workID) {
        this.workID = workID;
    }

    public String getStoreID() {
        return storeID;
    }

    public String getEmail() {
        return email;
    }

    public String getFull_name() {
        return full_name;
    }

    public boolean getIsOwner(){
        return isOwner;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
