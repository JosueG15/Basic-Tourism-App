package com.moviles.proyectofinal.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.services.FirebaseManager;
import com.moviles.proyectofinal.ui.adapter.TripAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyTripsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TripAdapter adapter;
    private List<Trip> tripsList = new ArrayList<>();
    private FirebaseManager firebaseManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        firebaseManager = new FirebaseManager();
        setupUI();
        setupBottomNavigationView();
        loadTrips();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
        bottomNavigationView.setSelectedItemId(R.id.navigation_location);
    }

    private void setupUI() {
        recyclerView = findViewById(R.id.rv_my_trips);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripAdapter(tripsList);
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.iv_back_button);
        backButton.setOnClickListener(v -> finish());

        findViewById(R.id.btn_create_new_trip).setOnClickListener(v -> startCreateTripActivity());
    }

    private void startCreateTripActivity() {
        Intent intent = new Intent(this, CreateTripActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.layout_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setOnItemReselectedListener(this::onNavigationItemReselected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int menuId = item.getItemId();

        if (menuId == R.id.navigation_home) {
            startActivity(new Intent(this, HomeActivity.class));
            return true;
        } else if (menuId == R.id.navigation_location) {
            return true;
        } else if (menuId == R.id.navigation_heart) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        } else if (menuId == R.id.navigation_person) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return false;
    }

    private void onNavigationItemReselected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_location) {
            scrollToTop(recyclerView);
        }
    }

    private void scrollToTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void loadTrips() {
        firebaseManager.fetchUserTrips(new FirebaseManager.FirebaseTripsCallback() {
            @Override
            public void onSuccess(List<Trip> trips) {
                tripsList.clear();
                tripsList.addAll(trips);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
            }
        });
    }
}
