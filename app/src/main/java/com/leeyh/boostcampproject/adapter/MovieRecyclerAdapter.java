package com.leeyh.boostcampproject.adapter;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.databinding.RecyclerViewItemBinding;
import com.leeyh.boostcampproject.model.MovieModel;

import java.util.ArrayList;


public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder> {

    private ArrayList<MovieModel> mItems = new ArrayList<>();
    private String mQuery;
    private int mTotalSize;
    private int mStartPoint;
    private int mDisplayCount;

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewItemBinding mBinding;

        MovieViewHolder(RecyclerViewItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerViewItemBinding binding = RecyclerViewItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder movieViewHolder, @SuppressLint("RecyclerView") final int i) {
        movieViewHolder.mBinding.setMovie(mItems.get(i));
        movieViewHolder.mBinding.executePendingBindings();
        movieViewHolder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse(mItems.get(i).getLink()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle animation = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.enter, R.anim.exit).toBundle();
                    view.getContext().startActivity(link, animation);
                } else {
                    view.getContext().startActivity(link);
                }
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MovieViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.mBinding.unbind();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(ArrayList<MovieModel> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public int getTotalSize() {
        return mTotalSize;
    }

    public void setTotalSize(int totalSize) {
        this.mTotalSize = totalSize;
    }

    public int getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(int startPoint) {
        this.mStartPoint = startPoint;
    }

    public int getDisplayCount() {
        return mDisplayCount;
    }

    public void setDisplayCount(int displayCount) {
        this.mDisplayCount = displayCount;
    }
}
