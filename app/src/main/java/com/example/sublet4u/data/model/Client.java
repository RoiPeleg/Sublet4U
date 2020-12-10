package com.example.sublet4u.data.model;

import android.widget.ImageView;

public class Client
{
    public String name;
    public String desc;
    public String clientID;
//    public ImageView imageView;
    public String sex;

    public Client(){
        this.name = "";
        this.desc = "";
        this.clientID = "";
    }

    public Client(String name1, String desc, String clientID, String sex){
        this.name = name1;
        this.desc = desc;
        this.clientID = clientID;
//        this.imageView  = imageView;
        this.sex = sex;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getClientID() {
        return clientID;
    }

    public String getName()
    {
        return this.name;
    }
}
