package com.moviles.proyectofinal.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.services.FirebaseManager;
import com.moviles.proyectofinal.ui.adapter.TripSummaryAdapter;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;

public class TripDetailsActivity extends AppCompatActivity {

    private TripViewModel tripViewModel;
    private FirebaseManager firebaseManager;
    private Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        firebaseManager = new FirebaseManager();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("trip")) {
            currentTrip = (Trip) intent.getSerializableExtra("trip");
            initializeTripViewModel(currentTrip);
            initializeView();
        } else {
            Toast.makeText(this, "Error loading trip details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeTripViewModel(Trip trip) {
        tripViewModel.getSelectedDestinations().setValue(trip.getSelectedDestinations());
        tripViewModel.getScheduledDestinations().setValue(trip.getScheduledDestinations());
        tripViewModel.getHotelReservations().setValue(trip.getHotelReservations());
        tripViewModel.getActivityReservations().setValue(trip.getActivityReservations());
    }

    private void initializeView() {
        TextView tripNameTextView = findViewById(R.id.tv_trip_name);
        TextView tripPeopleTextView = findViewById(R.id.tv_trip_people);

        tripNameTextView.setText(currentTrip.getTripName());
        tripPeopleTextView.setText(getString(R.string.my_trips_number_of_people, currentTrip.getNumberOfPeople()));

        RecyclerView recyclerView = findViewById(R.id.rv_summary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TripSummaryAdapter tripSummaryAdapter = new TripSummaryAdapter(tripViewModel);
        recyclerView.setAdapter(tripSummaryAdapter);

        Button btnDelete = findViewById(R.id.btn_delete_trip);
        Button btnEdit = findViewById(R.id.btn_edit_trip);
        Button btnBack = findViewById(R.id.btn_back);

        btnDelete.setOnClickListener(v -> deleteTrip());
        btnEdit.setOnClickListener(v -> editTrip());
        btnBack.setOnClickListener(v -> finish());
    }


    private void deleteTrip() {
        firebaseManager.deleteTrip(currentTrip.getTripId(), new FirebaseManager.FirebaseTripCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TripDetailsActivity.this, "Trip deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TripDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editTrip() {
        Intent intent = new Intent(this, CreateTripActivity.class);
        intent.putExtra(CreateTripActivity.EXTRA_TRIP, currentTrip);
        startActivity(intent);
        finish();
    }

}

