package com.moviles.proyectofinal.data.entity;

import java.util.Date;
import java.io.Serializable;

public class ScheduledDestination implements Serializable{
    private Date date;
    private PlacePrediction place;

    public ScheduledDestination() {
    }

    public ScheduledDestination(Date date, PlacePrediction place) {
        this.date = date;
        this.place = place;
    }

    public Date getDate() {
        return date;
    }

    public PlacePrediction getPlace() {
        return place;
    }
}
