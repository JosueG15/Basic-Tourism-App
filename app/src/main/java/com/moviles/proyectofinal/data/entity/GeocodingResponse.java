package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodingResponse {
    @SerializedName("results")
    private List<GeocodingResult> results;
    @SerializedName("status")
    private String status;

    public List<GeocodingResult> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public static class GeocodingResult {
        @SerializedName("formatted_address")
        private String formattedAddress;

        public String getFormattedAddress() {
            return formattedAddress;
        }
    }
}
