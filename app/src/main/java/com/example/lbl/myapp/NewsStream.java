package com.example.lbl.myapp;

import android.content.Context;
import android.util.Log;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewsStream {

    public static boolean writeNew(News[] news, Context context){
        String filePath = context.getFilesDir().getPath().toString() + "/news.txt";
        System.out.println(filePath);
        File file = new File(filePath);
        try{
            if(!file.exists()){
                System.out.println("file don't exist");
                file.createNewFile();
                System.out.println("file create successfully");
            }
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(news);
            output.close();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static News[] readNews(Context context){
        String filePath = context.getFilesDir().getPath().toString() + "/news.txt";
        System.out.println(filePath);
        File file = new File(filePath);
        try {
            if(!file.exists()){
                System.out.println("file don't exist");
                return null;
            }
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
                try {
                Object obj = input.readObject();
                input.close();
                News[] news = (News[])obj;
                return news;
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeBookmarks(News news, Context context){
        String filePath = context.getFilesDir().getPath().toString() + "/bookmarks.txt";
        System.out.println(filePath);
        File file = new File(filePath);
        try{
            if(!file.exists()){
                System.out.println("file don't exist");
                file.createNewFile();
                System.out.println("file create successfully");
            }
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file, true));
            output.writeObject(news);
            output.close();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static News[] readBookmarks(Context context){
        String filePath = context.getFilesDir().getPath().toString() + "/bookmarks.txt";
        System.out.println(filePath);
        File file = new File(filePath);
        try {
            if(!file.exists()){
                System.out.println("file don't exist");
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream input = new ObjectInputStream(fis);
            News[] bookmarks;
            List<News> bookmarksList = new ArrayList<>();
            try {
                Object obj = input.readObject();
                News news = (News) obj;
                bookmarksList.add(news);
                byte[] buf = new byte[4];
                while(true) {
                    fis.read(buf);
                    obj = input.readObject();
                    news = (News) obj;
                    bookmarksList.add(news);
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            catch (EOFException e) {
                e.printStackTrace();
                return null;
            }
            finally {
                input.close();
                bookmarks = new News[bookmarksList.size()];
                for(int i = 0; i < bookmarksList.size(); i++) {
                    bookmarks[i] = bookmarksList.get(i);
                }
                return bookmarks;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<News> delectBookmarks(List<News> newsList, int k,Context context) {
        newsList.remove(k);
        String filePath = context.getFilesDir().getPath().toString() + "/bookmarks.txt";
        System.out.println(filePath);
        File file = new File(filePath);
        file.delete();
        for(int i = 0; i < newsList.size(); i++) {
            writeBookmarks(newsList.get(i), context);
        }
        return newsList;
    }
}

