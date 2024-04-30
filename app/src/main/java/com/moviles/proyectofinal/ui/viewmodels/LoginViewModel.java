package com.moviles.proyectofinal.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moviles.proyectofinal.services.FirebaseManager;

public class LoginViewModel extends ViewModel {
    private FirebaseManager authManager = new FirebaseManager();
    private MutableLiveData<Boolean> userLoggedIn = new MutableLiveData<>();
    private MutableLiveData<String> loginStatus = new MutableLiveData<>();
    public LiveData<Boolean> getUserLoggedIn() {
        return userLoggedIn;
    }
    public LiveData<String> getLoginStatus() {
        return loginStatus;
    }
    public boolean isUserLoggedIn() {
        return authManager.isUserLoggedIn();
    }
    public void loginUser(String email, String password) {
        authManager.login(email, password, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(String message) {
                loginStatus.postValue("Success");
            }

            @Override
            public void onError(String error) {
                loginStatus.postValue(error);
            }
        });
    }
}

