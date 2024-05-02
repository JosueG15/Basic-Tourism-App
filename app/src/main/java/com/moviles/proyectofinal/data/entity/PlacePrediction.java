package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

public class PlacePrediction {
    @SerializedName("description")
    private String description;

    @SerializedName("place_id")
    private String placeId;

    public String getDescription() {
        return description;
    }

    public String getId() {
        return placeId;
    }

    @Override
    public String toString() {
        return description;
    }
}

