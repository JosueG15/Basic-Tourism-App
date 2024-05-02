package com.moviles.proyectofinal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlaceDetails;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PlaceDetails> favoritesList;
    private LayoutInflater inflater;
    private Context context;
    private OnPlaceClickListener listener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private boolean isLoaderVisible = false;

    public FavoritesAdapter(Context context, List<PlaceDetails> favoritesList, OnPlaceClickListener listener) {
        this.context = context;
        this.favoritesList = favoritesList;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = inflater.inflate(R.layout.skeleton_item_placeholder, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View itemView = inflater.inflate(R.layout.item_favorite_card, parent, false);
            return new FavoritesViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FavoritesViewHolder) {
            PlaceDetails placeDetails = favoritesList.get(position);
            ((FavoritesViewHolder) holder).bind(placeDetails);
        }
    }

    @Override
    public int getItemCount() {
        return favoritesList.size() + (isLoaderVisible ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == favoritesList.size() && isLoaderVisible) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setPlaces(List<PlaceDetails> newPlaces, boolean hasMore) {
        this.favoritesList.clear();
        this.favoritesList.addAll(newPlaces);
        this.isLoaderVisible = hasMore;
        notifyDataSetChanged();
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlaceName, tvCountry;
        private ImageView ivPlace;
        private Button btnViewPlace;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlace = itemView.findViewById(R.id.iv_place);
            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
            tvCountry = itemView.findViewById(R.id.tv_country);
            btnViewPlace = itemView.findViewById(R.id.btn_view_place);

            btnViewPlace.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlaceClick(favoritesList.get(position).getPlace().getPlaceId());
                }
            });
        }

        public void bind(PlaceDetails placeDetails) {
            tvPlaceName.setText(placeDetails.getPlace().getName());
            tvCountry.setText(placeDetails.getPlace().getVicinity());
            ivPlace.setImageBitmap(placeDetails.getPhoto());
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnPlaceClickListener {
        void onPlaceClick(String placeId);
    }
}
