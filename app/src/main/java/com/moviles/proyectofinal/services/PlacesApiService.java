package com.moviles.proyectofinal.services;

import com.moviles.proyectofinal.data.entity.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiService {
    @GET("place/autocomplete/json")
    Call<PlaceResponse> getAutocompletePlaces(
            @Query("input") String input,
            @Query("key") String apiKey
    );

    @GET("place/nearbysearch/json")
    Call<PlaceResponse> getNearbyPlaces(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("key") String apiKey,
            @Query("type") String type,
            @Query("pagetoken") String pageToken
    );

    @GET("place/details/json")
    Call<PlaceResponse> getPlaceDetails(
            @Query("place_id") String placeId,
            @Query("key") String apiKey
    );
}

