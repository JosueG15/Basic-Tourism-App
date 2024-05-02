package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceResponse {
    @SerializedName("predictions")
    private List<PlacePrediction> predictions;

    @SerializedName("results")
    private List<GooglePlace> results;

    @SerializedName("result")
    private GooglePlace result;

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

    public GooglePlace getResult() {
        return result;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
