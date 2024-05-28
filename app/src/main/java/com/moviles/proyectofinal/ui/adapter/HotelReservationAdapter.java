package com.moviles.proyectofinal.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.HotelReservation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HotelReservationAdapter extends RecyclerView.Adapter<HotelReservationAdapter.ViewHolder> {

    private List<HotelReservation> hotelReservations;
    private OnReservationRemovedListener onReservationRemovedListener;

    public HotelReservationAdapter(List<HotelReservation> hotelReservations, OnReservationRemovedListener onReservationRemovedListener) {
        this.hotelReservations = hotelReservations;
        this.onReservationRemovedListener = onReservationRemovedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HotelReservation reservation = hotelReservations.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText(String.format("%s - %s", dateFormat.format(reservation.getStartDate()), dateFormat.format(reservation.getEndDate())));
        holder.tvPlaceName.setText(reservation.getHotel().getDescription());

        holder.btnRemove.setOnClickListener(v -> {
            hotelReservations.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, hotelReservations.size());
            onReservationRemovedListener.onReservationRemoved(reservation);
        });
    }

    @Override
    public int getItemCount() {
        return hotelReservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvPlaceName;
        Button btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }

    public interface OnReservationRemovedListener {
        void onReservationRemoved(HotelReservation reservation);
    }
}
