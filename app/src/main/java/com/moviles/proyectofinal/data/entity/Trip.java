package com.moviles.proyectofinal.data.entity;

import java.util.List;
import java.io.Serializable;

public class Trip implements Serializable {
    private String tripId;
    private String tripName;
    private int numberOfPeople;
    private List<PlacePrediction> selectedDestinations;
    private List<ScheduledDestination> scheduledDestinations;
    private List<HotelReservation> hotelReservations;
    private List<ScheduledDestination> activityReservations;
    public Trip() {
    }
    public Trip(String tripId, String tripName, int numberOfPeople, List<PlacePrediction> selectedDestinations,
                List<ScheduledDestination> scheduledDestinations, List<HotelReservation> hotelReservations,
                List<ScheduledDestination> activityReservations) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.numberOfPeople = numberOfPeople;
        this.selectedDestinations = selectedDestinations;
        this.scheduledDestinations = scheduledDestinations;
        this.hotelReservations = hotelReservations;
        this.activityReservations = activityReservations;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public List<PlacePrediction> getSelectedDestinations() {
        return selectedDestinations;
    }

    public void setSelectedDestinations(List<PlacePrediction> selectedDestinations) {
        this.selectedDestinations = selectedDestinations;
    }

    public List<ScheduledDestination> getScheduledDestinations() {
        return scheduledDestinations;
    }

    public void setScheduledDestinations(List<ScheduledDestination> scheduledDestinations) {
        this.scheduledDestinations = scheduledDestinations;
    }

    public List<HotelReservation> getHotelReservations() {
        return hotelReservations;
    }

    public void setHotelReservations(List<HotelReservation> hotelReservations) {
        this.hotelReservations = hotelReservations;
    }

    public List<ScheduledDestination> getActivityReservations() {
        return activityReservations;
    }

    public void setActivityReservations(List<ScheduledDestination> activityReservations) {
        this.activityReservations = activityReservations;
    }
}
