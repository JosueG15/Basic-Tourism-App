package com.moviles.proyectofinal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.GooglePlaceReview;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<GooglePlaceReview> reviews;
    private Context context;

    public ReviewAdapter(Context context, List<GooglePlaceReview> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_card, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        GooglePlaceReview review = reviews.get(position);
        holder.tvReviewerName.setText(review.author);
        holder.tvReviewTime.setText(review.timeDescription);
        holder.tvReviewContent.setText(review.content != null && !review.content.isEmpty() ? review.content : "Pendiente de comentar");
        holder.tvReviewRating.setText(String.valueOf(review.rating));
        Glide.with(context)
                .load(review.profilePhotoUrl)
                .placeholder(R.drawable.ic_profile)
                .into(holder.ivReviewerPhoto);
    }

    public void setReviews(List<GooglePlaceReview> reviews) {
        this.reviews = reviews;
    }


    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewTime, tvReviewContent, tvReviewRating;
        ImageView ivReviewerPhoto;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tv_reviewer_name);
            tvReviewTime = itemView.findViewById(R.id.tv_review_time);
            tvReviewContent = itemView.findViewById(R.id.tv_review_content);
            tvReviewRating = itemView.findViewById(R.id.tv_review_rating);
            ivReviewerPhoto = itemView.findViewById(R.id.iv_reviewer_photo);
        }
    }
}
