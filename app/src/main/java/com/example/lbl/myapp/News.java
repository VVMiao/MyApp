package com.example.lbl.myapp;

import java.io.Serializable;

public class News implements Serializable{

    private static final long serialVersionUID = 1L;
    private String name;
    private String uri;
    public MyBitmap myBitmap;
    private String picuri;

    public News(String name, String uri, MyBitmap myBitmap, String picuri) {
        this.name = name;
        this.uri = uri;
        this.myBitmap = myBitmap;
        this.picuri = picuri;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public MyBitmap getMyBitmap() {
        return myBitmap;
    }

    public String getPicuri() {
        return picuri;
    }

    private void setName(String name){
        this.name=name;
    }

    private void setUri(String uri){
        this.uri=uri;
    }

    private void setMyBitmap(MyBitmap myBitmap){
        this.myBitmap=myBitmap;
    }

}
