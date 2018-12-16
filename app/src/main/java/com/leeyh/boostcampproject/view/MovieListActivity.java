package com.leeyh.boostcampproject.view;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.adapter.MovieRecyclerAdapter;
import com.leeyh.boostcampproject.databinding.ActivityBindingMovieListMainBinding;
import com.leeyh.boostcampproject.viewmodel.MovieListViewModel;

public class MovieListActivity extends AppCompatActivity {

    ActivityBindingMovieListMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_binding_movie_list_main);
        MovieListViewModel.MovieViewModelClassFactory factory = new MovieListViewModel.MovieViewModelClassFactory(this);
        final MovieListViewModel handler = ViewModelProviders.of(this, factory).get(MovieListViewModel.class);
        mBinding.setMainHandler(handler);
        mBinding.setLifecycleOwner(this);

        MovieRecyclerAdapter recyclerAdapter = new MovieRecyclerAdapter(this);
        mBinding.movieResultRecyclerView.setAdapter(recyclerAdapter);
        mBinding.movieResultRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }
}
