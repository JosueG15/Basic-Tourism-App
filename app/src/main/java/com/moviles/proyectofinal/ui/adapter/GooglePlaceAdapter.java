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

public class GooglePlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PlaceDetails> placeDetailsList;
    private LayoutInflater inflater;

    private FirebaseManager firebaseManager;
    private Context context;
    private OnPlaceClickListener listener;

    private boolean isLoaderVisible = true;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public GooglePlaceAdapter(Context context, List<PlaceDetails> placeDetailsList, OnPlaceClickListener listener) {
        this.context = context;
        this.placeDetailsList = placeDetailsList;
        this.inflater = LayoutInflater.from(context);
        this.firebaseManager = new FirebaseManager();
        this.listener = listener;
    }

    public void addPlaces(List<PlaceDetails> newPlaces, boolean hasMore) {
        firebaseManager.loadFavoritesAndUpdate(newPlaces, new FirebaseManager.FirebaseCallback() {
            @Override
            public void onSuccess(String message) {
                int startInsertIndex = placeDetailsList.size();
                placeDetailsList.addAll(newPlaces);
                isLoaderVisible = hasMore;
                notifyItemRangeInserted(startInsertIndex, newPlaces.size());
                if (!hasMore) {
                    notifyItemRemoved(placeDetailsList.size());
                }
            }

            @Override
            public void onError(String error) {
                Log.e("Adapter", "Error al cargar favoritos: " + error);
                int startInsertIndex = placeDetailsList.size();
                placeDetailsList.addAll(newPlaces);
                isLoaderVisible = hasMore;
                notifyItemRangeInserted(startInsertIndex, newPlaces.size());
                if (!hasMore) {
                    notifyItemRemoved(placeDetailsList.size());
                }
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        if (position == placeDetailsList.size() && isLoaderVisible) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skeleton_item_placeholder, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View itemView = inflater.inflate(R.layout.item_place_card, parent, false);
            return new GooglePlaceViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GooglePlaceViewHolder) {
            if (position < placeDetailsList.size()) {
                GooglePlaceViewHolder viewHolder = (GooglePlaceViewHolder) holder;
                PlaceDetails placeDetails = placeDetailsList.get(position);
                viewHolder.bind(placeDetails);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (placeDetailsList == null) {
            return 0;
        }
        return placeDetailsList.size() + (isLoaderVisible ? 1 : 0);
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

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    PlaceDetails placeDetails = placeDetailsList.get(position);
                    listener.onPlaceClicked(placeDetails.getPlace().getPlaceId());
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setPlaces(List<PlaceDetails> newPlaces) {
        placeDetailsList.clear();
        placeDetailsList.addAll(newPlaces);
        notifyDataSetChanged();
    }

    public interface OnPlaceClickListener {
        void onPlaceClicked(String placeId);
    }

}
