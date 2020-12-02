package com.example.sublet4u.data.model;

public class Apartment {
    public String name;
    public  String desc;
    public String address;
    public String ownerID;
    public int price;

    public Apartment(){
        this.name = "";
        this.desc = "";
        this.address = "";
        this.ownerID = "";
        this.price = 0;
    }

    public Apartment(String name1, String desc, String address, String ownerID, int price){
        this.name = name1;
        this.desc = desc;
        this.address = address;
        this.ownerID = ownerID;
        this.price = price;
    }
}