package com.example.serveIt;

public class Table {
    private int ID;
    private String status;

    public Table(int ID, String status) {
        this.ID = ID;
        this.status = status;
    }

    public Table() {

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
