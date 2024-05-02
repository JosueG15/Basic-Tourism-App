package com.moviles.proyectofinal.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GooglePlaceAddress {

    @SerializedName("long_name")
    public String longName;

    @SerializedName("types")
    public List<String> types;

    public String getLongName() { return longName; }

    public List<String> getTypes() { return types; }
}
