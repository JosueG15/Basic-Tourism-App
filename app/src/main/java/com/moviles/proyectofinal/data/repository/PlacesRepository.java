package com.moviles.proyectofinal.data.repository;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.moviles.proyectofinal.BuildConfig;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.GooglePhotos;
import com.moviles.proyectofinal.data.entity.GooglePlace;
import com.moviles.proyectofinal.data.entity.PlaceDetails;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.entity.PlaceResponse;
import com.moviles.proyectofinal.services.PlacesApiService;
import com.moviles.proyectofinal.utils.constants.HomeConstants;
import com.moviles.proyectofinal.utils.network.GoogleLanguageInterceptor;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

public class PlacesRepository {
    private static PlacesRepository instance;
    private PlacesApiService apiService;
    private FusedLocationProviderClient locationClient;
    private PlacesClient placesClient;
    private Context context;
    private MutableLiveData<List<String>> searchResults = new MutableLiveData<>();
    private String nextPageToken = "";

    private void extractPlacesWithPhotos(PlaceResponse response, MutableLiveData<List<PlaceDetails>> placesLiveData) {
        if (response.getResults().isEmpty()) {
            placesLiveData.postValue(new ArrayList<>());
            return;
        }

        List<PlaceDetails> placeDetailsList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(response.getResults().size());

        for (GooglePlace place : response.getResults()) {
            if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
                GooglePhotos photo = place.getPhotos().get(0);
                fetchPhotoBitmap(photo.getPhotoReference(), 400, new PhotoLoadCallback() {
                    @Override
                    public void onPhotoLoaded(Bitmap photo) {
                        placeDetailsList.add(new PlaceDetails(place, photo, response.getNextPageToken()));
                        if (counter.decrementAndGet() == 0) {
                            placesLiveData.postValue(placeDetailsList);
                        }
                    }

                    @Override
                    public void onError() {
                        placeDetailsList.add(new PlaceDetails(place, getPlaceholderImage(), response.getNextPageToken()));
                        if (counter.decrementAndGet() == 0) {
                            placesLiveData.postValue(placeDetailsList);
                        }
                    }
                });
            } else {
                placeDetailsList.add(new PlaceDetails(place, getPlaceholderImage(), response.getNextPageToken()));
                if (counter.decrementAndGet() == 0) {
                    placesLiveData.postValue(placeDetailsList);
                }
            }
        }
    }

    private void fetchPhotoBitmap(String photoReference, int maxWidth, PhotoLoadCallback callback) {
        PhotoMetadata metadata = PhotoMetadata.builder(photoReference).build();
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(metadata)
                .setMaxWidth(maxWidth)
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
            Bitmap photo = fetchPhotoResponse.getBitmap();
            callback.onPhotoLoaded(photo);
        }).addOnFailureListener(e -> {
            Log.e("Photo Download Error", "Error fetching photo: " + e.getMessage());
            callback.onError();
        });
    }

    private PlacesRepository(Context context) {
        this.context = context;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new GoogleLanguageInterceptor()).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.GOOGLE_PLACES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        apiService = retrofit.create(PlacesApiService.class);
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
        this.placesClient = Places.createClient(context); // Initialize PlacesClient
    }

    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getLocality() + ", " + address.getCountryName();
            } else {
                return "No address found";
            }
        } catch (IOException e) {
            Log.e("Geocoder Error", "Failed to get address from location: " + e.getMessage());
            return "No address found";
        }
    }

    private Bitmap getPlaceholderImage() {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_default_place);
    }

    private List<String> processAutocompleteResults(PlaceResponse response) {
        return response.getPredictions().stream().map(PlacePrediction::getDescription).collect(Collectors.toList());
    }

    public static synchronized PlacesRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PlacesRepository(context);
        }
        return instance;
    }

    public LiveData<List<String>> getSearchResults() {
        return searchResults;
    }

    public LiveData<String> getCurrentLocationFormatted() {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            liveData.postValue("Permission not granted");
            return liveData;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                liveData.postValue(getAddressFromLocation(location));
            } else {
                liveData.postValue("Location not available");
            }
        }).addOnFailureListener(e -> liveData.postValue("Failed to get location: " + e.getMessage()));

        return liveData;
    }

    public void getAutocompletePlaces(String input) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Call<PlaceResponse> call = apiService.getAutocompletePlaces(input, BuildConfig.GOOGLE_PLACES_API_KEY);
            call.enqueue(new Callback<PlaceResponse>() {
                @Override
                public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        searchResults.postValue(processAutocompleteResults(response.body()));
                    } else {
                        searchResults.postValue(Collections.singletonList("Search failed: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<PlaceResponse> call, Throwable t) {
                    searchResults.postValue(Collections.singletonList("API call failed: " + t.getMessage()));
                }
            });
        });
    }

    public LiveData<Location> getLiveLocation() {
        MutableLiveData<Location> liveData = new MutableLiveData<>();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            liveData.postValue(null);
            return liveData;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                liveData.postValue(location);
            } else {
                liveData.postValue(null);
            }
        }).addOnFailureListener(e -> {
            liveData.postValue(null);
        });

        return liveData;
    }

    public LiveData<String> getLocationString() {
        MutableLiveData<String> locationStringLiveData = new MutableLiveData<>();
        LiveData<Location> liveLocation = getLiveLocation();

        liveLocation.observeForever(location -> {
            if (location != null) {
                String locationString = location.getLatitude() + "," + location.getLongitude();
                locationStringLiveData.postValue(locationString);
            } else {
                locationStringLiveData.postValue("Location not available");
            }
        });

        return locationStringLiveData;
    }

    public LiveData<List<PlaceDetails>> getNearbyPlaces(String type, String pageToken) {
        MutableLiveData<List<PlaceDetails>> placesLiveData = new MutableLiveData<>();
        LiveData<String> locationLiveData = getLocationString();

        locationLiveData.observeForever(newLocation -> {
            if (newLocation != null && !newLocation.isEmpty()) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    Call<PlaceResponse> call = apiService.getNearbyPlaces(newLocation, HomeConstants.SEARCH_RADIUS, BuildConfig.GOOGLE_PLACES_API_KEY, type, pageToken);
                    call.enqueue(new Callback<PlaceResponse>() {
                        @Override
                        public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                extractPlacesWithPhotos(response.body(), placesLiveData);
                                nextPageToken = response.body().getNextPageToken();  // Assuming this is part of your response
                            } else {
                                Log.e("API Error", "Search failed: " + response.message());
                                placesLiveData.postValue(new ArrayList<>());
                            }
                        }

                        @Override
                        public void onFailure(Call<PlaceResponse> call, Throwable t) {
                            Log.e("API Failure", "API call failed: " + t.getMessage());
                            placesLiveData.postValue(new ArrayList<>());
                        }
                    });
                });
            } else {
                placesLiveData.postValue(new ArrayList<>()); // Location not available
            }
        });

        return placesLiveData;
    }

    public interface PhotoLoadCallback {
        void onPhotoLoaded(Bitmap photo);
        void onError();
    }
}
