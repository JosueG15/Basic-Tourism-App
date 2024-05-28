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

public class ActivityReservationAdapter extends RecyclerView.Adapter<ActivityReservationAdapter.ViewHolder> {

    private List<ScheduledDestination> activityReservations;
    private TripViewModel tripViewModel;

    public ActivityReservationAdapter(List<ScheduledDestination> activityReservations, TripViewModel tripViewModel) {
        this.activityReservations = activityReservations;
        this.tripViewModel = tripViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduledDestination activity = activityReservations.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.tvDate.setText(dateFormat.format(activity.getDate()));
        holder.tvActivityName.setText(activity.getPlace().getDescription());

        holder.btnDelete.setOnClickListener(v -> {
            tripViewModel.removeActivityReservation(activity);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return activityReservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvActivityName;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvActivityName = itemView.findViewById(R.id.tv_activity_name);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
