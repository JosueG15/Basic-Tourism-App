package com.moviles.proyectofinal.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.BitmapStorage;

public class ImageFullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);

        PhotoView photoView = findViewById(R.id.photo_view);
        Bitmap image = BitmapStorage.getImage();
        if (image != null) {
            photoView.setImageBitmap(image);
        }

        photoView.setMaximumScale(4.0f);
        photoView.setMediumScale(3.0f);
        photoView.setMinimumScale(1.0f);

        photoView.setOnClickListener(v -> finish());
    }
}
