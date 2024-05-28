package com.moviles.proyectofinal.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.ui.activities.TripDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;

    public TripAdapter(List<Trip> trips) {
        this.trips = (trips != null) ? trips : new ArrayList<>();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_my_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {

        private TextView tripName;
        private TextView numberOfPeople;
        private TextView viewButton;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.tv_trip_name);
            numberOfPeople = itemView.findViewById(R.id.tv_number_of_people);
            viewButton = itemView.findViewById(R.id.btn_view_trip);
        }

        public void bind(Trip trip) {
            tripName.setText(trip.getTripName());
            numberOfPeople.setText(itemView.getContext().getString(R.string.my_trips_number_of_people, trip.getNumberOfPeople()));
            viewButton.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), TripDetailsActivity.class);
                intent.putExtra("trip", trip);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
