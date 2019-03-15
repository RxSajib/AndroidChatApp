package com.example.quickchat;

public class Contact_GetSet {

    String name;
    String statas;
    String image;

    public Contact_GetSet(){

    }

    public Contact_GetSet(String name, String statas, String image) {
        this.name = name;
        this.statas = statas;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatas() {
        return statas;
    }

    public void setStatas(String statas) {
        this.statas = statas;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
