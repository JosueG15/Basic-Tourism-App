package com.moviles.proyectofinal.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.HotelReservation;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.entity.ScheduledDestination;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TripSummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PLACE = 0;
    private static final int TYPE_ITINERARY = 1;
    private static final int TYPE_HOTEL = 2;
    private static final int TYPE_ACTIVITY = 3;

    private TripViewModel tripViewModel;

    public TripSummaryAdapter(TripViewModel tripViewModel) {
        this.tripViewModel = tripViewModel;
    }

    @Override
    public int getItemViewType(int position) {
        int placeCount = tripViewModel.getSelectedDestinations().getValue().size();
        int itineraryCount = tripViewModel.getScheduledDestinations().getValue().size();
        int hotelCount = tripViewModel.getHotelReservations().getValue().size();

        if (position < placeCount) {
            return TYPE_PLACE;
        } else if (position < placeCount + itineraryCount) {
            return TYPE_ITINERARY;
        } else if (position < placeCount + itineraryCount + hotelCount) {
            return TYPE_HOTEL;
        } else {
            return TYPE_ACTIVITY;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PLACE || viewType == TYPE_ITINERARY) {
            View view = inflater.inflate(R.layout.item_scheduled_destination, parent, false);
            return new ScheduledDestinationAdapter.ViewHolder(view);
        } else if (viewType == TYPE_HOTEL) {
            View view = inflater.inflate(R.layout.item_hotel_reservation, parent, false);
            return new HotelReservationAdapter.ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_activity_reservation, parent, false);
            return new ActivityReservationAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int placeCount = tripViewModel.getSelectedDestinations().getValue().size();
        int itineraryCount = tripViewModel.getScheduledDestinations().getValue().size();
        int hotelCount = tripViewModel.getHotelReservations().getValue().size();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        if (holder.getItemViewType() == TYPE_PLACE) {
            PlacePrediction place = tripViewModel.getSelectedDestinations().getValue().get(position);
            ScheduledDestinationAdapter.ViewHolder placeHolder = (ScheduledDestinationAdapter.ViewHolder) holder;
            placeHolder.tvDate.setText("Lugar seleccionado");
            placeHolder.tvPlaceName.setText(place.getDescription());
            placeHolder.btnDelete.setVisibility(View.GONE);
        } else if (holder.getItemViewType() == TYPE_ITINERARY) {
            int adjustedPosition = position - placeCount;
            ScheduledDestination destination = tripViewModel.getScheduledDestinations().getValue().get(adjustedPosition);
            ScheduledDestinationAdapter.ViewHolder itineraryHolder = (ScheduledDestinationAdapter.ViewHolder) holder;
            itineraryHolder.tvDate.setText(dateFormat.format(destination.getDate()));
            itineraryHolder.tvPlaceName.setText(destination.getPlace().getDescription());
            itineraryHolder.btnDelete.setVisibility(View.GONE);
        } else if (holder.getItemViewType() == TYPE_HOTEL) {
            int adjustedPosition = position - placeCount - itineraryCount;
            HotelReservation reservation = tripViewModel.getHotelReservations().getValue().get(adjustedPosition);
            HotelReservationAdapter.ViewHolder hotelHolder = (HotelReservationAdapter.ViewHolder) holder;
            hotelHolder.tvDate.setText(String.format("%s - %s", dateFormat.format(reservation.getStartDate()), dateFormat.format(reservation.getEndDate())));
            hotelHolder.tvPlaceName.setText(reservation.getHotel().getDescription());
            hotelHolder.btnRemove.setVisibility(View.GONE);
        } else {
            int adjustedPosition = position - placeCount - itineraryCount - hotelCount;
            ScheduledDestination activity = tripViewModel.getActivityReservations().getValue().get(adjustedPosition);
            ActivityReservationAdapter.ViewHolder activityHolder = (ActivityReservationAdapter.ViewHolder) holder;
            activityHolder.tvDate.setText(dateFormat.format(activity.getDate()));
            activityHolder.tvActivityName.setText(activity.getPlace().getDescription());
            activityHolder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tripViewModel.getSelectedDestinations().getValue().size() +
                tripViewModel.getScheduledDestinations().getValue().size() +
                tripViewModel.getHotelReservations().getValue().size() +
                tripViewModel.getActivityReservations().getValue().size();
    }
}
