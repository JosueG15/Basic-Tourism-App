package com.moviles.proyectofinal.data.entity;

import java.util.Date;
import java.io.Serializable;

public class HotelReservation implements Serializable {
    private Date startDate;
    private Date endDate;
    private PlacePrediction hotel;

    public HotelReservation() {
    }

    public HotelReservation(Date startDate, Date endDate, PlacePrediction hotel) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.hotel = hotel;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public PlacePrediction getHotel() {
        return hotel;
    }
}
