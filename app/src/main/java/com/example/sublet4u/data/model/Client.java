package com.example.sublet4u.data.model;

import android.widget.ImageView;

public class Client
{
    public String desc;
    public String clientID;
    public String sex;

    public Client(){
        this.desc = "";
        this.clientID = "";
    }

    public Client(String desc, String clientID, String sex){
        this.desc = desc;
        this.clientID = clientID;
        this.sex = sex;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getClientID() {
        return clientID;
    }

}