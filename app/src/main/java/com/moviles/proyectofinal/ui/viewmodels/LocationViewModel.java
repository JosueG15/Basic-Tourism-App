package com.moviles.proyectofinal.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moviles.proyectofinal.data.entity.PlaceDetails;
import com.moviles.proyectofinal.data.repository.PlacesRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationViewModel extends AndroidViewModel {
    private PlacesRepository placesRepository;
    private LiveData<String> currentLocation;
    private MutableLiveData<List<PlaceDetails>> nearbyPlacesLiveData = new MutableLiveData<>();
    private List<PlaceDetails> currentPlaceDetails = new ArrayList<>();

    private String nextPageToken = "";

    public LocationViewModel(@NonNull Application application) {
        super(application);
        placesRepository = PlacesRepository.getInstance(application);
        fetchInitialLocation();
    }

    private void fetchInitialLocation() {
        currentLocation = placesRepository.getCurrentLocationFormatted();
    }

    public void searchAutocompletePlaces(String input) {
        placesRepository.getAutocompletePlaces(input);
    }

    public LiveData<List<String>> getSearchResults() {
        return placesRepository.getSearchResults();
    }

    public LiveData<String> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<List<PlaceDetails>> getNearbyPlaces(String type) {
        placesRepository.getNearbyPlaces(type, "").observeForever(newPlaces -> {
            currentPlaceDetails.clear();
            currentPlaceDetails.addAll(newPlaces);
            nearbyPlacesLiveData.postValue(new ArrayList<>(currentPlaceDetails));
            nextPageToken = newPlaces.get(0).getNextPageToken();
        });
        return nearbyPlacesLiveData;
    }

    public void loadMorePlaces(String type) {
        if (nextPageToken != null && !nextPageToken.isEmpty()) {
            placesRepository.getNearbyPlaces(type, nextPageToken).observeForever(newPlaces -> {
                if (!newPlaces.isEmpty()) {
                    nearbyPlacesLiveData.postValue(new ArrayList<>(newPlaces));
                    nextPageToken = newPlaces.get(newPlaces.size() - 1).getNextPageToken();
                    if (nextPageToken == null) {
                        nextPageToken = "";
                    }
                } else {
                    nextPageToken = "";
                }
            });
        }
    }






    public LiveData<List<PlaceDetails>> observeNearbyPlaces() {
        return nearbyPlacesLiveData;
    }
}
