package com.example.lbl.myapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class MyBitmap implements Serializable {
    private static final long serialVersionUID = 1L;
    private byte[] bitmapBytes = null;
    private String name = null;

    public MyBitmap(byte[] bitmapBytes, String name) {
        // TODO Auto-generated constructor stub
        this.bitmapBytes = bitmapBytes;
        this.name = name;
    }

    public byte[] getBitmapBytes() {
        return this.bitmapBytes;
    }

    public String getName() {
        return this.name;
    }

    private void setBitmapBytes(byte[] bitmapBytes) {
        this.bitmapBytes=bitmapBytes;
    }

    private void setName(String name) {
        this.name=name;
    }

    public static Bitmap getBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream baops = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baops);
        return baops.toByteArray();
    }

}
