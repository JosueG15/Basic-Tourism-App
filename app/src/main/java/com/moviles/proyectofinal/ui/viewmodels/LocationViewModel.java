package com.moviles.proyectofinal.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moviles.proyectofinal.data.entity.GooglePlaceReview;
import com.moviles.proyectofinal.data.entity.PlaceDetails;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.repository.PlacesRepository;
import com.moviles.proyectofinal.services.FirebaseManager;

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

    private MutableLiveData<List<GooglePlaceReview>> reviewsLiveData = new MutableLiveData<>();

    private String nextPageToken = "";

    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();



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

    public LiveData<List<PlacePrediction>> getSearchResults() {
        return placesRepository.getSearchResults();
    }

    public LiveData<PlaceDetails> getPlaceDetailsWithAllPhotos(String placeId) {
        return placesRepository.getPlaceDetailsWithAllPhotos(placeId);
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<String> getSuccessMessageLiveData() {
        return successMessageLiveData;
    }

    public LiveData<List<GooglePlaceReview>> getReviewsLiveData() {
        return reviewsLiveData;
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

    public void loadFavoritePlacesDetails() {
        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.fetchFavoritePlaceIds(new FirebaseManager.FirebaseFavoriteCallback() {
            @Override
            public void onSuccessPlaceIds(List<String> placeIds) {
                if (!placeIds.isEmpty()) {
                    placesRepository.getPlacesDetails(placeIds).observeForever(placesDetails -> {
                        nearbyPlacesLiveData.postValue(placesDetails);
                    });
                } else {
                    nearbyPlacesLiveData.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {
                nearbyPlacesLiveData.postValue(new ArrayList<>());
            }
        });
    }

    public void loadReviews(String placeId, List<GooglePlaceReview> googleReviews) {
        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.fetchReviews(placeId, new FirebaseManager.FirebaseReviewsCallback() {
            @Override
            public void onSuccessReviews(List<GooglePlaceReview> firebaseReviews) {
                List<GooglePlaceReview> combinedReviews = googleReviews != null ? new ArrayList<>(googleReviews) : new ArrayList<>();

                if (firebaseReviews != null) {
                    combinedReviews.addAll(firebaseReviews);
                }

                reviewsLiveData.postValue(combinedReviews);
            }

            @Override
            public void onError(String error) {
                reviewsLiveData.postValue(googleReviews);
            }
        });
    }

    public void submitReview(String placeId, GooglePlaceReview review) {
        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.saveReview(placeId, review, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(String message) {
                successMessageLiveData.postValue(message);
                loadReviews(placeId, reviewsLiveData.getValue());
            }

            @Override
            public void onError(String error) {
                errorMessageLiveData.postValue(error);
            }
        });
    }


}
