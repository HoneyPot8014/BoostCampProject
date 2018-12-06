package com.leeyh.boostcampproject.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MovieResultAdapter extends RecyclerView.ViewHolder<RecyclerView.ViewHolder> {

    private static class MovieViewHolder extends RecyclerView.ViewHolder {


        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public MovieResultAdapter(@NonNull View itemView) {
        super(itemView);
    }
}
