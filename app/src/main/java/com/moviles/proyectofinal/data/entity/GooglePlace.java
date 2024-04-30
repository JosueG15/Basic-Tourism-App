package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GooglePlace {
    @SerializedName("name")
    private String name;

    @SerializedName("place_id")
    private String placeId;

    @SerializedName("reference")
    private String reference;

    @SerializedName("photos")
    private List<GooglePhotos> photos;

    @SerializedName("vicinity")
    public String vicinity;


    @SerializedName( "rating")
    public Double rating;

    @SerializedName("user_ratings_total")
    public int totalRatings;

    public GooglePlace(String name, String placeId, String reference, String vicinity, List<GooglePhotos> photos, Double rating, int totalRatings) {
        this.name = name;
        this.placeId = placeId;
        this.reference = reference;
        this.vicinity = vicinity;
        this.photos = photos;
        this.rating = rating;
        this.totalRatings = totalRatings;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setPhotos(List<GooglePhotos> photos) {
        this.photos = photos;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public List<GooglePhotos> getPhotos() {
        return photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return  vicinity;
    }

    public Double getRating() {
        return rating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public String getReference() {
        return reference;
    }
}
