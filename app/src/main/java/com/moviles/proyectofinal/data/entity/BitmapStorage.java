package com.moviles.proyectofinal.data.entity;

import android.graphics.Bitmap;

public class BitmapStorage {
    private static Bitmap image;

    public static Bitmap getImage() {
        return image;
    }

    public static void setImage(Bitmap img) {
        image = img;
    }
}

