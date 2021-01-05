package com.example.sublet4u.data.model;

import org.jetbrains.annotations.NotNull;

public class Apartment {
    public String name;
    public  String desc;
    public String address;
    public String ownerID;
    public double grade;
    public double invertedGrade;
    public int price;
    public int discount;
    public boolean isOnSale;

    public Apartment(){
        this.name = "";
        this.desc = "";
        this.address = "";
        this.ownerID = "";
        this.price = 0;
        this.discount = 100;
        this.isOnSale = false;
        this.grade = 0;
        this.invertedGrade = 0;
    }

    public Apartment(String name1, String desc, String address, String ownerID, int price){
        this.name = name1;
        this.desc = desc;
        this.address = address;
        this.ownerID = ownerID;
        this.price = price;
        this.invertedGrade = 0;
    }

    @NotNull
    public String toString(){
        return  "Name: " + this.name + "\nDescription: "  + this.desc + "\nAddress: "+ this.address + "\nPrice: "+ this.price;
    }
}
