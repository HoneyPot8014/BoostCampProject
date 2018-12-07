package com.leeyh.boostcampproject.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leeyh.boostcampproject.databinding.RecyclerViewItemBinding;
import com.leeyh.boostcampproject.model.MovieModel;


public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder> {

    private ObservableArrayList<MovieModel> items = new ObservableArrayList<>();

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
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, @SuppressLint("RecyclerView") final int i) {
        movieViewHolder.mBinding.setMovie(items.get(i));
        movieViewHolder.mBinding.executePendingBindings();
        movieViewHolder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(i).getLink()));
                view.getContext().startActivity(link);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ObservableArrayList<MovieModel> movieDataList) {
        items = movieDataList;
        notifyDataSetChanged();
    }

}
