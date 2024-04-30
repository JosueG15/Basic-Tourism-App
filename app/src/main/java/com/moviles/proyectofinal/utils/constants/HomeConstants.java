package com.moviles.proyectofinal.utils.constants;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.GooglePlaceCategory;

import java.util.Arrays;
import java.util.List;

public class HomeConstants {
    public static final int SEARCH_RADIUS = 5000;
    public static final List<GooglePlaceCategory> CATEGORIES = Arrays.asList(
            new GooglePlaceCategory("Hoteles", "lodging", R.drawable.ic_hotel),
            new GooglePlaceCategory("Centros comerciales", "shopping_mall", R.drawable.ic_shopping_mall),
            new GooglePlaceCategory("Museos", "museum", R.drawable.ic_museum),
            new GooglePlaceCategory("Parques", "park", R.drawable.ic_park),
            new GooglePlaceCategory("Restaurantes", "restaurant", R.drawable.ic_restaurant),
            new GooglePlaceCategory("Belleza", "beauty_salon", R.drawable.ic_beauty),
            new GooglePlaceCategory("Bus", "bus_station", R.drawable.ic_bus),
            new GooglePlaceCategory("Cafes", "cafe", R.drawable.ic_coffee),
            new GooglePlaceCategory("Renta de autos", "car_rental", R.drawable.ic_rental),
            new GooglePlaceCategory("Casinos", "casino", R.drawable.ic_casino),
            new GooglePlaceCategory("Iglesias", "church", R.drawable.ic_church),
            new GooglePlaceCategory("Farmacias", "drugstore", R.drawable.ic_pharmacy),
            new GooglePlaceCategory("Estaciones de Gas", "gas_station", R.drawable.ic_gas_station),
            new GooglePlaceCategory("Naturaleza", "natural_feature", R.drawable.ic_lake),
            new GooglePlaceCategory("Gym", "gym", R.drawable.ic_gym),
            new GooglePlaceCategory("Licorerias", "liquor_store", R.drawable.ic_liquor),
            new GooglePlaceCategory("Cines", "movie_theater", R.drawable.ic_movies)

    );
}
