package com.leeyh.boostcampproject.adapter;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.leeyh.boostcampproject.databinding.RecyclerViewItemBinding;
import com.leeyh.boostcampproject.helper.ListDiffUtil;
import com.leeyh.boostcampproject.model.MovieModel;
import com.leeyh.boostcampproject.viewmodel.MainActivityViewModel;

import java.util.ArrayList;


public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder> {

    private ArrayList<MovieModel> mItems = new ArrayList<>();
    private FragmentActivity mActivity;

    public MovieRecyclerAdapter(FragmentActivity activity) {
        mActivity = activity;
    }

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
        //instance ViewModel and bind
        MainActivityViewModel.MovieViewModelClassFactory factory =
                new MainActivityViewModel.MovieViewModelClassFactory(movieViewHolder.mBinding.getRoot().getContext());
        MainActivityViewModel viewModel = ViewModelProviders.of(mActivity, factory).get(MainActivityViewModel.class);
        RecyclerViewItemBinding binding = movieViewHolder.mBinding;
        binding.setItemHandler(viewModel);
        binding.setMovie(mItems.get(i));
        binding.executePendingBindings();
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
        ListDiffUtil diffCallback = new ListDiffUtil(this.mItems, items);
        final DiffUtil.DiffResult newItems = DiffUtil.calculateDiff(diffCallback);
        this.mItems.clear();
        this.mItems.addAll(items);
        newItems.dispatchUpdatesTo(this);
    }
}
