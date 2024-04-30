package com.moviles.proyectofinal.data.entity;

import android.graphics.Bitmap;

public class PlaceDetails {
    private GooglePlace place;
    private String nextPageToken;
    private Bitmap photo;
    private boolean isFavorite;

    public PlaceDetails(GooglePlace place, Bitmap photo, String token) {
        this.place = place;
        this.photo = photo;
        this.nextPageToken = token;
        this.isFavorite = false;
    }

    public GooglePlace getPlace() {
        return place;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
