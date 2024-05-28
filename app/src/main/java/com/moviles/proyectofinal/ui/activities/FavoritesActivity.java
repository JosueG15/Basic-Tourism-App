package com.moviles.proyectofinal.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlaceDetails;
import com.moviles.proyectofinal.ui.adapter.FavoritesAdapter;
import com.moviles.proyectofinal.ui.viewmodels.LocationViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;

    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_heart);
        favoritesRecyclerView = findViewById(R.id.rv_favorite_places);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        setupInsets();
        setupBackButton();
        setupBottomNavigationView();
        setupRecyclers();
        loadFavorites();
    }

    private void loadFavorites() {
        locationViewModel.loadFavoritePlacesDetails();
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void setupBackButton() {
        ImageView backButton = findViewById(R.id.iv_back_button);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setOnItemReselectedListener(this::onNavigationItemReselected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int menuId = item.getItemId();

        if (menuId == R.id.navigation_home) {
            startActivity(new Intent(this, HomeActivity.class));
            return true;
        } else if (menuId == R.id.navigation_location) {
            startActivity(new Intent(this, MyTripsActivity.class));
            return true;
        } else if (menuId == R.id.navigation_heart) {
            return true;
        } else if (menuId == R.id.navigation_person) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return false;
    }


    private void onNavigationItemReselected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_heart) {
            scrollToTop(favoritesRecyclerView);
        }
    }

    private void scrollToTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }
    private void updateFavoritesList(List<PlaceDetails> favoritePlaces) {
        favoritesAdapter.setPlaces(favoritePlaces, false);
    }

    private void setupRecyclers() {
        favoritesAdapter = new FavoritesAdapter(this, new ArrayList<>(), this::navigateToPlaceActivity);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        locationViewModel.observeNearbyPlaces().observe(this, this::updateFavoritesList);
    }

    private void navigateToPlaceActivity(String placeId) {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("placeId", placeId);
        startActivity(intent);
    }
}
