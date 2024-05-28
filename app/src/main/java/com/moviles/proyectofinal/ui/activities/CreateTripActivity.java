package com.moviles.proyectofinal.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.ui.fragments.CreateTripStepOneFragment;

public class CreateTripActivity extends AppCompatActivity {
    public static final String EXTRA_TRIP = "trip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        if (savedInstanceState == null) {
            Trip trip = (Trip) getIntent().getSerializableExtra(EXTRA_TRIP);
            CreateTripStepOneFragment fragment = CreateTripStepOneFragment.newInstance(trip);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
