package com.example.quickchat;

public class MgetSet
{


    private String name, statas;
    private String image;

    public MgetSet(){

    }

    public MgetSet(String name, String statas, String image) {
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

    public void setImage(String image){
        this.image = image;
    }

    public String getStatas() {
        return statas;
    }

    public String getImage(){
        return image;
    }

    public void setStatas(String statas) {
        this.statas = statas;
    }
}
