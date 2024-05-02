package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

public class GooglePlaceReview {

    @SerializedName("author_name")
    public String author;

    @SerializedName("profile_photo_url")
    public String profilePhotoUrl;

    @SerializedName("rating")
    public double rating;

    @SerializedName("relative_time_description")
    public String timeDescription;

    @SerializedName("text")
    public String content;
}
