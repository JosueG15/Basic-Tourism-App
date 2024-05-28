package com.moviles.proyectofinal.services;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moviles.proyectofinal.data.entity.GooglePlaceReview;
import com.moviles.proyectofinal.data.entity.PlaceDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseManager {
    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;
    private final StorageReference mStorage;

    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
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

    public void fetchFavoritePlaceIds(FirebaseFavoriteCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference favoritesRef = mDatabase.child("favorites").child(userId);
            favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> placeIds = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
                            placeIds.add(snapshot.getKey());
                        }
                    }
                    callback.onSuccessPlaceIds(placeIds);
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

    public void saveReview(String placeId, GooglePlaceReview review, FirebaseCallback callback) {
        DatabaseReference reviewRef = mDatabase.child("reviews").child(placeId).push();
        reviewRef.setValue(review)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Review guardada correctamente."))
                .addOnFailureListener(e -> callback.onError("Error al guardar review: " + e.getMessage()));
    }

    public void fetchReviews(String placeId, FirebaseReviewsCallback callback) {
        DatabaseReference reviewsRef = mDatabase.child("reviews").child(placeId);
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GooglePlaceReview> reviews = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GooglePlaceReview review = snapshot.getValue(GooglePlaceReview.class);
                    reviews.add(review);
                }
                callback.onSuccessReviews(reviews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Error al cargar reviews: " + error.getMessage());
            }
        });
    }

    public void uploadProfileImage(Uri filePath, FirebaseCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference profileImageRef = mDatabase.child("users").child(userId).child("profileImage");
            profileImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String existingImageUrl = dataSnapshot.getValue(String.class);
                    if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                        StorageReference existingImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(existingImageUrl);
                        existingImageRef.delete().addOnSuccessListener(aVoid -> {
                            uploadNewProfileImage(filePath, userId, callback);
                        }).addOnFailureListener(e -> {
                            callback.onError("Error al eliminar la imagen existente: " + e.getMessage());
                        });
                    } else {
                        uploadNewProfileImage(filePath, userId, callback);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onError("Error al cargar la imagen de perfil existente: " + databaseError.getMessage());
                }
            });
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }

    public void getProfileImage(ImageView imageView, FirebaseImageCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(userId).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.getValue(String.class);
                    if (uri != null) {
                        Glide.with(imageView.getContext())
                                .load(uri)
                                .into(imageView);
                        callback.onImageLoaded(Uri.parse(uri));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError("Error al cargar imagen de perfil: " + error.getMessage());
                }
            });
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }

    private void uploadNewProfileImage(Uri filePath, String userId, FirebaseCallback callback) {
        StorageReference newImageRef = mStorage.child("profile_images/" + userId + ".jpg");
        newImageRef.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> newImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    mDatabase.child("users").child(userId).child("profileImage").setValue(uri.toString())
                            .addOnSuccessListener(aVoid -> callback.onSuccess("Imagen de perfil subida correctamente."))
                            .addOnFailureListener(e -> callback.onError("Error al guardar URL de la imagen: " + e.getMessage()));
                }))
                .addOnFailureListener(e -> callback.onError("Error al subir imagen: " + e.getMessage()));
    }

    public void deleteUserAccount(FirebaseCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);

            userRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mAuth.getCurrentUser().delete().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            callback.onSuccess("Cuenta eliminada correctamente.");
                        } else {
                            callback.onError("Error al eliminar la cuenta: " + Objects.requireNonNull(task1.getException()).getMessage());
                        }
                    });
                } else {
                    callback.onError("Error al eliminar los datos del usuario: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }

    public void reauthenticateUser(FirebaseCallback callback, String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess("Reautenticación exitosa.");
                } else {
                    callback.onError("Error en la reautenticación: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }

    public void saveUserProfile(String name, String country, int age, String profile, FirebaseCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);
            userRef.child("name").setValue(name);
            userRef.child("country").setValue(country);
            userRef.child("age").setValue(age);
            userRef.child("profile").setValue(profile)
                    .addOnSuccessListener(aVoid -> callback.onSuccess("Perfil actualizado correctamente."))
                    .addOnFailureListener(e -> callback.onError("Error al actualizar el perfil: " + e.getMessage()));
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }

    public void getUserProfile(FirebaseUserProfileCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String country = dataSnapshot.child("country").getValue(String.class);
                    Integer age = dataSnapshot.child("age").getValue(Integer.class);
                    String profile = dataSnapshot.child("profile").getValue(String.class);

                    if (name != null && country != null && age != null && profile != null) {
                        callback.onSuccess(name, country, age, profile);
                    } else {
                        callback.onError("No se encontraron datos de perfil.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError("Error al cargar los datos del perfil: " + error.getMessage());
                }
            });
        } else {
            callback.onError("Usuario no autenticado.");
        }
    }

    public interface FirebaseUserProfileCallback {
        void onSuccess(String name, String country, int age, String profile);
        void onError(String error);
    }



    public interface FirebaseImageCallback {
        void onImageLoaded(Uri uri);
        void onError(String error);
    }

    public interface FirebaseReviewsCallback {
        void onSuccessReviews(List<GooglePlaceReview> reviews);
        void onError(String error);
    }


    public interface FirebaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface FirebaseFavoriteCallback {
        void onSuccessPlaceIds(List<String> placesIds);

        void onError(String error);
    }
}

