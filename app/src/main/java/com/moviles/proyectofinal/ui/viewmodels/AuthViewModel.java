package com.moviles.proyectofinal.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.moviles.proyectofinal.services.FirebaseManager;

public class AuthViewModel extends ViewModel {
    private final FirebaseManager firebaseAuthManager = new FirebaseManager();
    private MutableLiveData<Boolean> userLoggedIn = new MutableLiveData<>();
    private MutableLiveData<Boolean> registrationSuccessful = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public void register(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.postValue("Email y contraseña no pueden estar vacíos.");
            return;
        }

        firebaseAuthManager.createAccount(email, password, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(String message) {
                registrationSuccessful.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }
    public void login(String email, String password, FirebaseManager.FirebaseCallback callback) {
        firebaseAuthManager.login(email, password, callback);
    }
    public LiveData<Boolean> getUserLoggedIn() {
        return userLoggedIn;
    }
    public LiveData<Boolean> getRegistrationSuccessful() {
        return registrationSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public boolean isUserLoggedIn() {
        return firebaseAuthManager.isUserLoggedIn();
    }
}

