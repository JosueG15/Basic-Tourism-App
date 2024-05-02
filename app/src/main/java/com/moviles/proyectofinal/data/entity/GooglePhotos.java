package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

public class GooglePhotos {

    @SerializedName("height")
    private int height;

    @SerializedName("photo_reference")
    private String photoReference;

    @SerializedName("width")
    private int width;

    public GooglePhotos(int height, String photoReference, int width) {
        this.height = height;
        this.photoReference = photoReference;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
