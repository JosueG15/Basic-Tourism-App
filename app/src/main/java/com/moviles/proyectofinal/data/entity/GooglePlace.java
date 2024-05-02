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

    @SerializedName("address_components")
    public List<GooglePlaceAddress> addressComponents;

    @SerializedName("url")
    public String url;

    @SerializedName("reviews")
    public List<GooglePlaceReview> reviews;



    public GooglePlace(String name, String placeId, String reference, String vicinity, List<GooglePhotos> photos, Double rating, int totalRatings) {
        this.name = name;
        this.placeId = placeId;
        this.reference = reference;
        this.vicinity = vicinity;
        this.photos = photos;
        this.rating = rating;
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

    public List<GooglePlaceReview> getReviews() { return reviews;}

    public List<GooglePlaceAddress> getAddressComponents() { return addressComponents; }
}
