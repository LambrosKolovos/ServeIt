package com.example.serveIt;

public class User {

    private String email;
    private String full_name;
    private boolean isOwner;

    public User(String email, String full_name, boolean isOwner){
        this.email = email;
        this.full_name = full_name;
        this.isOwner = isOwner;
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
