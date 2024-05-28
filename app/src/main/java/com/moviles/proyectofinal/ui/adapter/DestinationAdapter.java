package com.moviles.proyectofinal.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlacePrediction;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder> {

    private List<PlacePrediction> destinations;
    private OnDestinationRemovedListener listener;

    public DestinationAdapter(List<PlacePrediction> destinations, OnDestinationRemovedListener listener) {
        this.destinations = destinations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_destination, parent, false);
        return new DestinationViewHolder(view);
    }

    public void updateDestinations(List<PlacePrediction> newDestinations) {
        this.destinations = newDestinations;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        PlacePrediction destination = destinations.get(position);
        holder.tvDestinationName.setText(destination.getDescription());
        holder.btnRemove.setOnClickListener(v -> {
            destinations.remove(position);
            notifyItemRemoved(position);
            listener.onDestinationRemoved();
        });
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    static class DestinationViewHolder extends RecyclerView.ViewHolder {
        TextView tvDestinationName;
        Button btnRemove;
        ImageView ivLocationIcon;

        public DestinationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDestinationName = itemView.findViewById(R.id.tv_destination_name);
            btnRemove = itemView.findViewById(R.id.btn_remove_destination);
            ivLocationIcon = itemView.findViewById(R.id.iv_location_icon);
        }
    }

    public interface OnDestinationRemovedListener {
        void onDestinationRemoved();
    }
}
