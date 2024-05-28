package com.moviles.proyectofinal.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.ScheduledDestination;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduledDestinationAdapter extends RecyclerView.Adapter<ScheduledDestinationAdapter.ViewHolder> {

    private List<ScheduledDestination> scheduledDestinations;
    private TripViewModel tripViewModel;

    public ScheduledDestinationAdapter(List<ScheduledDestination> scheduledDestinations, TripViewModel tripViewModel) {
        this.scheduledDestinations = scheduledDestinations;
        this.tripViewModel = tripViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scheduled_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduledDestination destination = scheduledDestinations.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText(dateFormat.format(destination.getDate()));
        holder.tvPlaceName.setText(destination.getPlace().getDescription());

        holder.btnDelete.setOnClickListener(v -> {
            tripViewModel.removeScheduledDestination(destination);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return scheduledDestinations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvPlaceName;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
