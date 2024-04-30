package com.moviles.proyectofinal.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.GooglePlaceCategory;

import java.util.List;

public class GoogleCategoryAdapter extends RecyclerView.Adapter<GoogleCategoryAdapter.CategoryViewHolder> {
    private List<GooglePlaceCategory> categories;
    private OnCategorySelectedListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public GoogleCategoryAdapter(List<GooglePlaceCategory> categories, OnCategorySelectedListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_category_card, parent, false);
        return new CategoryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        int currentPosition = holder.getBindingAdapterPosition();
        GooglePlaceCategory category = categories.get(currentPosition);
        holder.ivCategoryIcon.setImageResource(category.drawableId);
        holder.tvCategoryName.setText(category.userFriendlyName);

        boolean isSelected = selectedPosition == position;

        holder.itemView.setSelected(isSelected);
        holder.itemView.setBackgroundResource(isSelected ? R.drawable.bg_category_selector : R.drawable.bg_category_button);

        holder.itemView.setOnClickListener(v -> {
            if(selectedPosition == currentPosition) {
                listener.onCategorySelected("");
                notifyItemChanged(selectedPosition);
                selectedPosition = RecyclerView.NO_POSITION;
            } else {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = currentPosition;
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);

                listener.onCategorySelected(category.apiValue);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }

    public interface OnCategorySelectedListener {
        void onCategorySelected(String apiValue);
    }
}

