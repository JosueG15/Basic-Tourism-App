package com.moviles.proyectofinal.data.entity;

import com.google.android.libraries.places.api.model.Place;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceResponse {
    @SerializedName("predictions")
    private List<PlacePrediction> predictions;

    @SerializedName("results")
    private List<GooglePlace> results;

    @SerializedName("status")
    private String status;

    @SerializedName("next_page_token")
    private String nextPageToken;

    public List<PlacePrediction> getPredictions() {
        return predictions;
    }

    public List<GooglePlace> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
