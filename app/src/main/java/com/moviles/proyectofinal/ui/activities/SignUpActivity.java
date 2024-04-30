package com.moviles.proyectofinal.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.ui.viewmodels.AuthViewModel;

public class SignUpActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        setupObservers();
        setupInsets();
        setupUI();
        checkAutoLogin();
    }

    private void checkAutoLogin() {
        boolean rememberMe = prefs.getBoolean("RememberMe", false);
        if (rememberMe && authViewModel.isUserLoggedIn()) {
            navigateToHome();
        }
    }

    private void setupObservers() {
        authViewModel.getRegistrationSuccessful().observe(this, isSuccess -> {
            if (isSuccess) {
                navigateToHome();
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (!error.isEmpty()) {
                showMessage(error);
            }
        });
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupUI() {
        EditText etEmail = findViewById(R.id.et_email);
        EditText etUsername = findViewById(R.id.et_username);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLogin = findViewById(R.id.tv_login);

        btnRegister.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
            authViewModel.register(email, password);
        });

        tvLogin.setOnClickListener(view -> navigateToLogin());

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etUsername.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    public void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}