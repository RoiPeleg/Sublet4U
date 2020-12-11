package com.example.sublet4u.data.model;

public class Invitation {

    public String apartmentID;
    public String clientID;
    public String arriveDate;
    public String leaveDate;
    public int price;
    public String clientName;
    public String apartmentName;

    public Invitation(){
        this.apartmentID="";
        this.clientID="";
        this.arriveDate="";
        this.leaveDate="";
        this.price=0;
        this.clientName = "";
        this.apartmentName="";
    }

    public  Invitation (String apartmentID, String clientID, String arriveDate, String leaveDate, int price, String clientName, String apartmentName){
        this.apartmentID = apartmentID;
        this.clientID = clientID;
        this.arriveDate = arriveDate;
        this.leaveDate = leaveDate;
        this.price = price;
        this.clientName = clientName;
        this.apartmentName = apartmentName;
    }

    public String getClientID()
    {
        return this.clientID;
    }

    public String getApartmentID() {
        return this.apartmentID;
    }
}
