package com.example.sublet4u.data.model;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Invitation {

    public String apartmentID;
    public String clientID;
    public String arriveDate;
    public String leaveDate;
    public int price;
    public String clientName;
    public String apartmentName;
    public Boolean responded;

    public Invitation(){
        this.apartmentID="";
        this.clientID="";
        this.arriveDate="";
        this.leaveDate="";
        this.price=0;
        this.clientName = "";
        this.apartmentName="";
        this.responded = false;
    }

    public Invitation (String apartmentID, String clientID, String arriveDate, String leaveDate, int price, String clientName, String apartmentName){
        this.apartmentID = apartmentID;
        this.clientID = clientID;
        this.arriveDate = arriveDate;
        this.leaveDate = leaveDate;
        this.price = price;
        this.clientName = clientName;
        this.apartmentName = apartmentName;
        this.responded = false;
    }

    public String getClientID()
    {
        return this.clientID;
    }
    public String getLeaveDate()
    {
        return this.leaveDate;
    }
    public String getApartmentID() {
        return this.apartmentID;
    }
    public void respond(boolean response){
        responded = response;
    }
    @NotNull
    @Override
    public String toString(){
        return  "client: " + clientName + "\narriveDate: "  + arriveDate + "\nleaveDate: "+ leaveDate;
    }
}
