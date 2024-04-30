package com.moviles.proyectofinal.services;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moviles.proyectofinal.data.entity.PlaceDetails;

import java.util.List;
import java.util.Objects;

public class FirebaseManager {
    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;

    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void createAccount(String email, String password, FirebaseCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onError("Email y contraseña no pueden estar vacíos.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Registro exitoso.");
                    } else {
                        callback.onError("Registro fallido: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void login(String email, String password, FirebaseCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Inicio de sesión exitoso.");
                    } else {
                        callback.onError("Error de inicio de sesión: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void updateFavoriteInFirebase(String placeId, boolean isFavorite, FirebaseCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference favoriteRef = mDatabase.child("favorites").child(userId).child(placeId);
            if (isFavorite) {
                favoriteRef.setValue(true)
                        .addOnSuccessListener(aVoid -> callback.onSuccess("Favorito agregado correctamente."))
                        .addOnFailureListener(e -> callback.onError("Error al agregar a favoritos: " + e.getMessage()));
            } else {
                favoriteRef.removeValue()
                        .addOnSuccessListener(aVoid -> callback.onSuccess("Favorito eliminado correctamente."))
                        .addOnFailureListener(e -> callback.onError("Error al eliminar de favoritos: " + e.getMessage()));
            }
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }

    public void loadFavoritesAndUpdate(List<PlaceDetails> placeDetailsList, FirebaseCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference favoritesRef = mDatabase.child("favorites").child(userId);
            favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String placeId = snapshot.getKey();
                        for (PlaceDetails details : placeDetailsList) {
                            if (details.getPlace().getPlaceId().equals(placeId)) {
                                details.setFavorite(true);
                            }
                        }
                    }
                    callback.onSuccess("Favoritos cargados correctamente.");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError("Error al cargar favoritos: " + error.getMessage());
                }
            });
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }




    public interface FirebaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}

