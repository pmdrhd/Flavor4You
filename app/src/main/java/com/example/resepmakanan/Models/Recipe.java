package com.example.resepmakanan.Models;

import androidx.annotation.NonNull;

public class Recipe {
    private String id;
    private String Title;
    private String Image;
    private int servings;
    private int readyInMins;

    public Recipe(String id, String title, String image, int servings, int readyInMins) {
        this.id = id;
        Title = title;
        Image = image;
        this.servings = servings;
        this.readyInMins = readyInMins;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getImage(){
        return Image;
    }

    public int getServings() {
        return servings;
    }

    public int getReadyInMins() {
        return readyInMins;
    }

    @NonNull
    @Override
    public String toString() {
        return getTitle();
    }
}