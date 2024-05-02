package com.moviles.proyectofinal.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moviles.proyectofinal.BuildConfig;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
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
    private AutoCompleteTextView autoCompleteTextView;
    private BottomNavigationView bottomNavigationView;
    private String currentType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupElements();
        setupInsets();
        checkPermissions();
        setupObservers();
        setupBottomNavigationView();
        setupAdapters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        autoCompleteTextView.clearFocus();
        autoCompleteTextView.setText("");

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void setupElements() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_PLACES_API_KEY);
        }
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationTextView = findViewById(R.id.tv_location_default);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        autoCompleteTextView = findViewById(R.id.et_search);
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
            ArrayAdapter<PlacePrediction> adapter = new ArrayAdapter<PlacePrediction>(this, android.R.layout.simple_dropdown_item_1line, results) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = view.findViewById(android.R.id.text1);
                    PlacePrediction item = getItem(position);
                    if (item != null) {
                        textView.setText(item.getDescription());
                    }
                    return view;
                }
            };
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                PlacePrediction prediction = adapter.getItem(position);
                if (prediction != null) {
                    navigateToPlaceActivity(prediction.getId());
                }
            });
            if (!results.isEmpty()) {
                autoCompleteTextView.showDropDown();
            }
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
        placeAdapter = new GooglePlaceAdapter(this, new ArrayList<>(), this::navigateToPlaceActivity);
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
                placeAdapter.addPlaces(newPlaces, false);
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
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setOnItemReselectedListener(this::onNavigationItemReselected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int menuId = item.getItemId();

        if (menuId == R.id.navigation_home) {
            return true;
        } else if (menuId == R.id.navigation_location) {
            // Handle location action
            return true;
        } else if (menuId == R.id.navigation_chat) {
            // Handle chat action
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
        if (item.getItemId() == R.id.navigation_home) {
            scrollToTop(placesRecyclerView);
        }
    }

    private void scrollToTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void navigateToPlaceActivity(String placeId) {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("placeId", placeId);
        startActivity(intent);
    }

}
