package com.moviles.proyectofinal.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlaceDetails;
import com.moviles.proyectofinal.services.FirebaseManager;

import java.util.List;

public class GooglePlaceAdapter extends RecyclerView.Adapter<GooglePlaceAdapter.GooglePlaceViewHolder> {

    private List<PlaceDetails> placeDetailsList;
    private LayoutInflater inflater;

    private FirebaseManager firebaseManager;
    private Context context;

    public GooglePlaceAdapter(Context context, List<PlaceDetails> placeDetailsList) {
        this.context = context;
        this.placeDetailsList = placeDetailsList;
        this.inflater = LayoutInflater.from(context);
        this.firebaseManager = new FirebaseManager();
    }

    public void addPlaces(List<PlaceDetails> newPlaces) {
        firebaseManager.loadFavoritesAndUpdate(newPlaces, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(String message) {
                int startInsertIndex = placeDetailsList.size();
                placeDetailsList.addAll(newPlaces);
                notifyItemRangeInserted(startInsertIndex, newPlaces.size());
            }

            @Override
            public void onError(String error) {
                Log.e("Adapter", "Error al cargar favoritos: " + error);
                int startInsertIndex = placeDetailsList.size();
                placeDetailsList.addAll(newPlaces);
                notifyItemRangeInserted(startInsertIndex, newPlaces.size());
            }
        });
    }

    @NonNull
    @Override
    public GooglePlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_place_card, parent, false);
        return new GooglePlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GooglePlaceViewHolder holder, int position) {
        PlaceDetails placeDetails = placeDetailsList.get(position);
        holder.bind(placeDetails);
    }

    @Override
    public int getItemCount() {
        return placeDetailsList.size();
    }

    public class GooglePlaceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlaceName;
        private ImageView ivPlaceImage;
        private TextView tvRating;
        private TextView tvCountry;
        private ImageView ivFavorite;

        public GooglePlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
            ivPlaceImage = itemView.findViewById(R.id.iv_place);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvCountry = itemView.findViewById(R.id.tv_country);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);

            ivFavorite.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    PlaceDetails placeDetails = placeDetailsList.get(position);
                    boolean newFavoriteStatus = !placeDetails.isFavorite();
                    placeDetails.setFavorite(newFavoriteStatus);
                    firebaseManager.updateFavoriteInFirebase(placeDetails.getPlace().getPlaceId(), newFavoriteStatus, new FirebaseManager.FirebaseCallback() {
                        @Override
                        public void onSuccess(String message) {
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onError(String error) {
                            placeDetails.setFavorite(!newFavoriteStatus);
                            notifyItemChanged(position);
                            Log.e("Adapter", "Error al actualizar favoritos: " + error);
                        }
                    });
                }
            });
        }

        public void bind(PlaceDetails placeDetails) {
            tvPlaceName.setText(placeDetails.getPlace().getName());
            Bitmap placePhoto = placeDetails.getPhoto();
            if (placePhoto != null) {
                ivPlaceImage.setImageBitmap(placePhoto);
            } else {
                ivPlaceImage.setImageResource(R.drawable.img_ion_earth);
            }

            Double rating = placeDetails.getPlace().getRating();
            tvRating.setText(String.valueOf(rating != null ? rating : 0.0));
            tvCountry.setText(placeDetails.getPlace().getVicinity());

            ivFavorite.setImageResource(placeDetails.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite);
        }
    }

    public void setPlaces(List<PlaceDetails> newPlaces) {
        placeDetailsList.clear();
        placeDetailsList.addAll(newPlaces);
        notifyDataSetChanged();
    }

}
