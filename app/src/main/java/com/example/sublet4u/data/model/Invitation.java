package com.example.sublet4u.data.model;

public class Invitation {

    public String apartmentID;
    public String clientID;

    public Invitation(){
        this.apartmentID="";
        this.clientID="";
    }

    public  Invitation (String apartmentID, String clientID){
        this.apartmentID = apartmentID;
        this.clientID = clientID;
    }
}
