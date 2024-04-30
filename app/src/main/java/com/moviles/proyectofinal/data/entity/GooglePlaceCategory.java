package com.moviles.proyectofinal.data.entity;

public class GooglePlaceCategory {
    public String userFriendlyName;
    public String apiValue;
    public int drawableId;

    public GooglePlaceCategory(String userFriendlyName, String apiValue, int drawableId) {
        this.userFriendlyName = userFriendlyName;
        this.apiValue = apiValue;
        this.drawableId = drawableId;
    }
}
