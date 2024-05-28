package com.moviles.proyectofinal.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.GooglePlaceAddress;
import com.moviles.proyectofinal.data.entity.GooglePlaceReview;
import com.moviles.proyectofinal.ui.adapter.ImageSliderAdapter;
import com.moviles.proyectofinal.ui.adapter.ReviewAdapter;
import com.moviles.proyectofinal.ui.viewmodels.LocationViewModel;
import com.moviles.proyectofinal.utils.KeyboardUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity {
    private String placeId;
    private ViewPager2 viewPagerImages;
    private ImageSliderAdapter imageSliderAdapter;
    private TextView tvImageCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_places);

        setupInsets();
        setupElements();
        setupBackButton();
        setupPlaceDetails();
        setupViewPager();
        setupReviewDialog();
    }

    private void setupElements() {
        tvImageCounter = findViewById(R.id.tv_image_counter);
        viewPagerImages = findViewById(R.id.viewPagerImages);

        ScrollView scrollView = findViewById(R.id.cl_main);
        View rootView = findViewById(R.id.cl_main);

        KeyboardUtils.addKeyboardVisibilityListener(rootView, scrollView, isVisible -> {

        });

        LocationViewModel viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        viewModel.getSuccessMessageLiveData().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        viewModel.getErrorMessageLiveData().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupBackButton() {
        ImageView backButton = findViewById(R.id.iv_back_button);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupPlaceDetails() {
        placeId = getIntent().getStringExtra("placeId");
        LocationViewModel viewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        viewModel.getPlaceDetailsWithAllPhotos(placeId).observe(this, placeDetails -> {
            if (placeDetails != null && placeDetails.getPhotos() != null) {
                updateImageSlider(placeDetails.getPhotos());

                TextView tvPlaceName = findViewById(R.id.tv_place_name);
                TextView tvRating = findViewById(R.id.tv_rating);
                TextView tvLocation = findViewById(R.id.tv_location_default);

                tvPlaceName.setText(placeDetails.getPlace().getName());
                tvRating.setText(String.valueOf(placeDetails.getPlace().getRating() != null ? placeDetails.getPlace().getRating() : 0.0));
                tvLocation.setText(getCountryFromAddressComponents(placeDetails.getPlace().getAddressComponents()));

                tvImageCounter.setText(getString(R.string.places_image_count, 1, placeDetails.getPhotos().size()));

                viewModel.loadReviews(placeId, placeDetails.getPlace().getReviews());

                viewModel.getReviewsLiveData().observe(this, reviews -> {
                    RecyclerView recyclerView = findViewById(R.id.rv_reviews);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(new ReviewAdapter(this, reviews != null ? reviews : new ArrayList<>()));
                });
            }
        });
    }

    private void updateImageSlider(List<Bitmap> images) {
        imageSliderAdapter = new ImageSliderAdapter(this, images);
        viewPagerImages.setAdapter(imageSliderAdapter);
    }

    private void setupViewPager() {
        viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvImageCounter.setText(getString(R.string.places_image_count, position + 1, imageSliderAdapter.getItemCount()));
            }
        });
    }

    private String getCountryFromAddressComponents(List<GooglePlaceAddress> addressComponents) {
        for (GooglePlaceAddress component : addressComponents) {
            if (component.types.contains("country")) {
                return component.longName;
            }
        }
        return getString(R.string.home_location_text_default);
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        return formatter.format(new Date());
    }

    private void setupReviewDialog() {
        Button submitReviewButton = findViewById(R.id.btn_submit_review);
        EditText commentEditText = findViewById(R.id.et_review_input);

        submitReviewButton.setOnClickListener(view -> {
            String comment = commentEditText.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(this, "Por favor, deja un comentario antes de enviar tu calificación.", Toast.LENGTH_SHORT).show();
            } else {
                showReviewDialog();
            }
        });
    }

    private void showReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rating_dialog, null);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        Button buttonSubmit = dialogView.findViewById(R.id.btn_submit_rating);
        EditText commentEditText = findViewById(R.id.et_review_input);

        AlertDialog dialog = builder.create();

        buttonSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0) {
                Toast.makeText(this, "Por favor, selecciona una calificación antes de enviar.", Toast.LENGTH_SHORT).show();
            } else {
                GooglePlaceReview review = new GooglePlaceReview();
                review.author = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                review.profilePhotoUrl = null;
                review.rating = rating;
                review.timeDescription = getCurrentDate();
                review.content = commentEditText.getText().toString().trim();

                LocationViewModel viewModel = new ViewModelProvider(this).get(LocationViewModel.class);
                viewModel.submitReview(placeId, review);

                dialog.dismiss();
                commentEditText.setText("");
                ratingBar.setRating(0);
            }
        });

        dialog.show();
    }


}