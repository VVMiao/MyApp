package com.example.lbl.myapp;

import java.io.Serializable;

public class News implements Serializable{

    private static final long serialVersionUID = 1L;
    private String name;
    private String uri;
    private  MyBitmap myBitmap;

    public News(String name,String uri, MyBitmap myBitmap) {
        this.name = name;
        this.uri = uri;
        this.myBitmap = myBitmap;
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
