package com.moviles.proyectofinal.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.services.FirebaseManager;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private BottomNavigationView bottomNavigationView;
    private ImageView profileImageView;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        firebaseManager = new FirebaseManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_person);

        profileImageView = findViewById(R.id.iv_profile_picture);
        profileImageView.setOnClickListener(v -> openImagePicker());

        loadProfileImage();
        loadProfileInfo();
        setupModifyDataButton();
        setupDeleteAccountButton();
        setupInsets();
        setupLogoutButton();
        setupBottomNavigationView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_person);

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void loadProfileInfo() {
        firebaseManager.getUserProfile(new FirebaseManager.FirebaseUserProfileCallback() {
            @Override
            public void onSuccess(String name, String country, int age, String profile) {
                TextView tvName = findViewById(R.id.tv_name);
                TextView tvCountry = findViewById(R.id.tv_country);
                TextView tvTravelerType = findViewById(R.id.tv_traveler_type);

                tvName.setText(name);
                tvCountry.setText(country);
                tvTravelerType.setText(profile);

                findViewById(R.id.layout_profile_info).setVisibility(View.VISIBLE);
                findViewById(R.id.tv_pending_profile).setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                findViewById(R.id.layout_profile_info).setVisibility(View.GONE);
                findViewById(R.id.tv_pending_profile).setVisibility(View.VISIBLE);
            }
        });
    }


    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int menuId = item.getItemId();

        if (menuId == R.id.navigation_home) {
            startActivity(new Intent(this, HomeActivity.class));
            return true;
        } else if (menuId == R.id.navigation_location) {
            // Handle location action
            return true;
        } else if (menuId == R.id.navigation_heart) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        } else if (menuId == R.id.navigation_person) {
            return true;
        }
        return false;
    }

    private void setupLogoutButton() {
        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImageView.setImageBitmap(bitmap);
                uploadImageToFirebase(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(Uri filePath) {
        firebaseManager.uploadProfileImage(filePath, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage() {
        firebaseManager.getProfileImage(findViewById(R.id.iv_profile_picture), new FirebaseManager.FirebaseImageCallback() {
            @Override
            public void onImageLoaded(Uri uri) {
                // Imagen cargada correctamente
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupModifyDataButton() {
        Button modifyDataButton = findViewById(R.id.btn_modify_data);
        modifyDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupDeleteAccountButton() {
        Button deleteAccountButton = findViewById(R.id.btn_delete_account);
        deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Borrar cuenta")
                .setMessage("¿Está seguro de que desea eliminar su cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", (dialog, which) -> deleteAccount())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteAccount() {
        reauthenticateUser(new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(String message) {
                firebaseManager.deleteUserAccount(new FirebaseManager.FirebaseCallback() {
                    @Override
                    public void onSuccess(String message) {
                        showToast(message);
                        navigateToSignUp();
                    }

                    @Override
                    public void onError(String error) {
                        showToast(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                showToast(error);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void reauthenticateUser(FirebaseManager.FirebaseCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reautenticación");
        builder.setMessage("Por favor, ingresa tu contraseña para continuar:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String password = input.getText().toString();
            if (!password.isEmpty()) {
                firebaseManager.reauthenticateUser(new FirebaseManager.FirebaseCallback() {
                    @Override
                    public void onSuccess(String message) {
                        callback.onSuccess(message);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                }, password);
            } else {
                callback.onError("La contraseña no puede estar vacía.");
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(ProfileActivity.this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
