package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlacePrediction implements Serializable {

    public PlacePrediction() {
    }
    @SerializedName("description")
    private String description;

    @SerializedName("place_id")
    private String placeId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return placeId;
    }

    @Override
    public String toString() {
        return description;
    }
}

