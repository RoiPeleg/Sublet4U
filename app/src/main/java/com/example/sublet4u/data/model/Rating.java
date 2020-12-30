package com.example.sublet4u.data.model;

public class Rating
{
    public String invitationID;
    public String apartmentID;
    public String rateNumber;
    public String writeReview;
    public String commentOwner;

    public Rating(){
        invitationID ="";
        apartmentID = "";
    }

    public Rating(String invitationID, String apartmentID ,String rateNumber, String writeReview){
        this.invitationID = invitationID;
        this.apartmentID = apartmentID;
        this.rateNumber = rateNumber;
        this.writeReview = writeReview;
        this.commentOwner = "";
    }

    public String toString()
    {
        if (commentOwner.equals(""))
        {
            return  "invitationID: " + this.invitationID + "\nrateNumber: "+ this.rateNumber + "\nwriteReview: "+ this.writeReview;
        }
        else
            return  "invitationID: " + this.invitationID + "\nrateNumber: "+ this.rateNumber + "\nwriteReview: "+ this.writeReview + "\ncommentOwner: "+ this.commentOwner;
    }
}
