package com.moviles.proyectofinal.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.services.FirebaseManager;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;
    private EditText etName, etCountry;
    private TextInputLayout tilAge;
    private RadioGroup rgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        firebaseManager = new FirebaseManager();

        etName = findViewById(R.id.et_name);
        etCountry = findViewById(R.id.et_country);
        tilAge = findViewById(R.id.til_age);
        rgProfile = findViewById(R.id.rg_profile);

        loadUserProfile();

        setupSaveButton();
        setupBackButton();
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String country = etCountry.getText().toString();
            int age = Integer.parseInt(tilAge.getEditText().getText().toString());
            String profile = ((RadioButton) findViewById(rgProfile.getCheckedRadioButtonId())).getText().toString();

            firebaseManager.saveUserProfile(name, country, age, profile, new FirebaseManager.FirebaseCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    navigateToProfile();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupBackButton() {
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToProfile() {
        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
        finish();
    }

    private void loadUserProfile() {
        firebaseManager.getUserProfile(new FirebaseManager.FirebaseUserProfileCallback() {
            @Override
            public void onSuccess(String name, String country, int age, String profile) {
                etName.setText(name);
                etCountry.setText(country);
                tilAge.getEditText().setText(String.valueOf(age));

                if (profile.equals("Nómada")) {
                    rgProfile.check(R.id.rb_nomada);
                } else if (profile.equals("Turista")) {
                    rgProfile.check(R.id.rb_turista);
                } else if (profile.equals("Mochilero")) {
                    rgProfile.check(R.id.rb_mochilero);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
