package com.moviles.proyectofinal.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.BitmapStorage;
import com.moviles.proyectofinal.ui.activities.ImageFullScreenActivity;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {
    private List<Bitmap> images;
    private Context context;

    public ImageSliderAdapter(Context context, List<Bitmap> images) {
        this.context = context;
        this.images = images;

        if (images == null || images.isEmpty()) {
            this.images.add(getDefaultImage());
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhotoView photoView = new PhotoView(parent.getContext());
        photoView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ImageViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Bitmap image = images.get(position);
        holder.photoView.setImageBitmap(image);
        holder.photoView.setOnClickListener(v -> {
            BitmapStorage.setImage(image);
            Intent intent = new Intent(context, ImageFullScreenActivity.class);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private PhotoView photoView;

        public ImageViewHolder(@NonNull PhotoView v) {
            super(v);
            photoView = v;
        }
    }

    private Bitmap getDefaultImage() {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_default_place);
    }
}

