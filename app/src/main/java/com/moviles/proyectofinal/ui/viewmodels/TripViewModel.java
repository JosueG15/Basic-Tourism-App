package com.moviles.proyectofinal.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moviles.proyectofinal.data.entity.HotelReservation;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.entity.ScheduledDestination;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripViewModel extends ViewModel {

    private final MutableLiveData<List<PlacePrediction>> selectedDestinations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ScheduledDestination>> scheduledDestinations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<HotelReservation>> hotelReservations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ScheduledDestination>> activityReservations = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<PlacePrediction>> getSelectedDestinations() {
        return selectedDestinations;
    }

    public void setSelectedDestinations(List<PlacePrediction> destinations) {
        selectedDestinations.setValue(destinations);
    }

    public void setScheduledDestinations(List<ScheduledDestination> destinations) {
        scheduledDestinations.setValue(destinations);
    }

    public void setHotelReservations(List<HotelReservation> hotels) {
        hotelReservations.setValue(hotels);
    }

    public void setActivityReservations(List<ScheduledDestination> activities) {
        activityReservations.setValue(activities);
    }

    public void addActivityReservation(Date date, PlacePrediction activity) {
        List<ScheduledDestination> currentActivityReservations = activityReservations.getValue();
        if (currentActivityReservations != null) {
            currentActivityReservations.add(new ScheduledDestination(date, activity));
            activityReservations.setValue(currentActivityReservations);
        }
    }

    public void removeActivityReservation(ScheduledDestination activity) {
        List<ScheduledDestination> currentActivityReservations = activityReservations.getValue();
        if (currentActivityReservations != null) {
            currentActivityReservations.remove(activity);
            activityReservations.setValue(currentActivityReservations);
        }
    }

    public MutableLiveData<List<ScheduledDestination>> getActivityReservations() {
        return activityReservations;
    }

    public void addScheduledDestination(Date date, PlacePrediction place) {
        List<ScheduledDestination> currentScheduledDestinations = scheduledDestinations.getValue();
        if (currentScheduledDestinations != null) {
            currentScheduledDestinations.add(new ScheduledDestination(date, place));
            scheduledDestinations.setValue(currentScheduledDestinations);
        }
    }

    public MutableLiveData<List<ScheduledDestination>> getScheduledDestinations() {
        return scheduledDestinations;
    }

    public void addHotelReservation(Date startDate, Date endDate, PlacePrediction hotel) {
        List<HotelReservation> currentHotelReservations = hotelReservations.getValue();
        if (currentHotelReservations != null) {
            currentHotelReservations.add(new HotelReservation(startDate, endDate, hotel));
            hotelReservations.setValue(currentHotelReservations);
        }
    }

    public MutableLiveData<List<HotelReservation>> getHotelReservations() {
        return hotelReservations;
    }

    public boolean isDateTimeSlotAvailable(Date date) {
        List<ScheduledDestination> currentDestinations =scheduledDestinations.getValue();
        if (currentDestinations != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String newDate = dateFormat.format(date);
            String newTime = timeFormat.format(date);

            for (ScheduledDestination destination : currentDestinations) {
                String existingDate = dateFormat.format(destination.getDate());
                String existingTime = timeFormat.format(destination.getDate());

                if (newDate.equals(existingDate) && newTime.equals(existingTime)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isHotelDateTimeSlotAvailable(Date startDate, Date endDate) {
        List<HotelReservation> currentHotelReservations = hotelReservations.getValue();
        if (currentHotelReservations != null) {
            for (HotelReservation reservation : currentHotelReservations) {
                Date existingStartDate = reservation.getStartDate();
                Date existingEndDate = reservation.getEndDate();

                if (startDate.before(existingEndDate) && endDate.after(existingStartDate)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void removeScheduledDestination(ScheduledDestination destination) {
        List<ScheduledDestination> currentScheduledDestinations = scheduledDestinations.getValue();
        if (currentScheduledDestinations != null) {
            currentScheduledDestinations.remove(destination);
            scheduledDestinations.setValue(currentScheduledDestinations);
        }
    }

    public void removeHotelReservation(HotelReservation destination) {
        List<HotelReservation> currentHotelReservations = hotelReservations.getValue();
        if (currentHotelReservations != null) {
            currentHotelReservations.remove(destination);
            hotelReservations.setValue(currentHotelReservations);
        }
    }
}
