package com.oude.dndclub.bean;

public class CommonList
{
    private String name;
    private int imageId;
    public CommonList(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
