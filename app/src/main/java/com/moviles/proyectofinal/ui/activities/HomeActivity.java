package com.moviles.proyectofinal.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moviles.proyectofinal.BuildConfig;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.ui.adapter.GoogleCategoryAdapter;
import com.moviles.proyectofinal.ui.adapter.GooglePlaceAdapter;
import com.moviles.proyectofinal.ui.viewmodels.LocationViewModel;
import com.moviles.proyectofinal.utils.constants.HomeConstants;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationViewModel locationViewModel;
    private TextView locationTextView;
    private GoogleCategoryAdapter categoryAdapter;
    private RecyclerView placesRecyclerView;
    private GooglePlaceAdapter placeAdapter;
    private String currentType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_PLACES_API_KEY);
        }

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationTextView = findViewById(R.id.tv_location_default);

        setupInsets();
        checkPermissions();
        setupObservers();
        setupBottomNavigationView();
        setupAdapters();
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            initializeLocationFeatures();
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso de ubicación requerido")
                .setMessage("Esta aplicación necesita el permiso de ubicación para funcionar correctamente. Por favor, permite el acceso a la ubicación.")
                .setPositiveButton("Configuración", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Salir", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocationFeatures();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private void initializeLocationFeatures() {
        locationViewModel.getCurrentLocation().observe(this, location -> {
            locationTextView.setText(location);
        });
    }

    private void setupObservers() {
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.et_search);
        locationViewModel.getSearchResults().observe(this, results -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, results);
            autoCompleteTextView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    locationViewModel.searchAutocompletePlaces(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupAdapters() {
        RecyclerView recyclerView = findViewById(R.id.rv_category_list);
        categoryAdapter = new GoogleCategoryAdapter(HomeConstants.CATEGORIES, this::onCategorySelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(categoryAdapter);

        placesRecyclerView = findViewById(R.id.rv_top_trip_list);
        placeAdapter = new GooglePlaceAdapter(this, new ArrayList<>());
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        placesRecyclerView.setAdapter(placeAdapter);

        placesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollHorizontally(1)) {
                    loadMorePlaces();
                }
            }
        });

        locationViewModel.getNearbyPlaces("").observe(this, newPlaces -> {
            if (placeAdapter != null) {
                placeAdapter.addPlaces(newPlaces);
            }
        });
    }

    private void onCategorySelected(String apiValue) {
        currentType = apiValue;
        locationViewModel.getNearbyPlaces(currentType).observe(this, newPlaces -> {
            if (placeAdapter != null) {
                placeAdapter.setPlaces(newPlaces);
            }
        });
    }

    private void loadMorePlaces() {
        locationViewModel.loadMorePlaces(currentType);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                // Navega a Home
                return true;
            } else if (id == R.id.navigation_location) {
                // Navega a Location
                return true;
            } else if (id == R.id.navigation_chat) {
                // Navega a Chat
                return true;
            } else if (id == R.id.navigation_heart) {
                // Navega a Heart
                return true;
            } else if (id == R.id.navigation_person) {
                // Navega a Person
                return true;
            }
            return false;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                scrollToTop(placesRecyclerView);  // Asumiendo placesRecyclerView es el RecyclerView relevante
            }
        });
    }

    private void scrollToTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

}
