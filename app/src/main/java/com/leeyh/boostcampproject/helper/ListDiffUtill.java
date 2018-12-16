package com.leeyh.boostcampproject.helper;

import android.support.v7.util.DiffUtil;

import com.leeyh.boostcampproject.model.MovieModel;

import java.util.ArrayList;

public class ListDiffUtill extends DiffUtil.Callback {

    private ArrayList<MovieModel> mOldList;
    private ArrayList<MovieModel> mNewList;

    public ListDiffUtill(ArrayList<MovieModel> oldMovieList, ArrayList<MovieModel> newMovieList) {
        this.mOldList = oldMovieList;
        this.mNewList = newMovieList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return mOldList.get(i).getTitle().equals(mNewList.get(i1).getTitle());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return mOldList.get(i).equals(mOldList.get(i1));
    }
}
