package com.example.sublet4u.data.model;

import org.jetbrains.annotations.NotNull;

public class Respond {
    public String invitationID;
    public boolean status;
    public String ap_name;

    public Respond(){
        invitationID ="";
    }

    public Respond(String invitationID,boolean status,String ap_name){
        this.invitationID = invitationID;
        this.status = status;
        this.ap_name = ap_name;
    }

    @NotNull
    @Override
    public String toString(){
        String s = " ";
        if(!status){
            s += "Sorry your offer was declined for apartment " + ap_name;
        }else{
            s +="your offer was accepted for apartment " + ap_name;
        }
        return invitationID + s;
    }
}
