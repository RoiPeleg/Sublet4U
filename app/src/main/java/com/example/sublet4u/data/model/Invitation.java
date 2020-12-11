package com.example.sublet4u.data.model;

public class Invitation {

    public String apartmentID;
    public String clientID;
    public String arriveDate;
    public String leaveDate;

    public Invitation(){
        this.apartmentID="";
        this.clientID="";
        this.arriveDate="";
        this.leaveDate="";
    }

    public  Invitation (String apartmentID, String clientID, String arriveDate, String leaveDate){
        this.apartmentID = apartmentID;
        this.clientID = clientID;
        this.arriveDate = arriveDate;
        this.leaveDate = leaveDate;
    }

    public String getClientID()
    {
        return this.clientID;
    }

    public String getApartmentID() {
        return this.apartmentID;
    }
}
