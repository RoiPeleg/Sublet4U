package com.example.sublet4u.data.model;

public class Message {

        private String id;
        private String text;
        private String Sender;
        private String ReceiverID;
        private String SenderID;
        private String photoUrl;
        private String imageUrl;

        public Message() {
        }

        public Message(String text, String Sender, String ReceiverID,String SenderID, String photoUrl, String imageUrl) {
            this.text = text;
            this.ReceiverID = ReceiverID;
            this.Sender = Sender;
            this.SenderID = SenderID;
            this.photoUrl = photoUrl;
            this.imageUrl = imageUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSender() {
            return Sender;
        }

        public void setSender(String name) {
            this.Sender = name;
        }

        public String getReceiverID() {
            return ReceiverID;
        }

        public void setReceiverID(String ID) {
            this.ReceiverID = ID;
        }
        public String getSenderID() {
            return SenderID;
        }

        public void setSenderID(String ID) {
            this.SenderID = ID;
        }
        public String getPhotoUrl() {
            return photoUrl;
        }

        public String getText() {
            return text;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

