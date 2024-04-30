package com.moviles.proyectofinal.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.moviles.proyectofinal.ui.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        setupInsets();
        setupObservers();
        setupUI();
        checkAutoLogin();
    }

    private void setupUI() {
        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvCreateAccount = findViewById(R.id.tv_create_account);
        CheckBox ckRememberMe = findViewById(R.id.ck_remember);


        btnLogin.setOnClickListener(view -> {
            String email = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                loginViewModel.loginUser(email, password);
                if (ckRememberMe.isChecked()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("RememberMe", true);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("RememberMe");
                    editor.apply();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });

        tvCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void checkAutoLogin() {
        boolean rememberMe = prefs.getBoolean("RememberMe", false);
        if (rememberMe && loginViewModel.isUserLoggedIn()) {
            navigateToHome();
        }
    }

    private void setupObservers() {
        loginViewModel.getUserLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                navigateToHome();
            }
        });

        loginViewModel.getLoginStatus().observe(this, status -> {
            if ("Success".equals(status)) {
                navigateToHome();
            } else {
                Toast.makeText(this, status, Toast.LENGTH_LONG).show();
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

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}