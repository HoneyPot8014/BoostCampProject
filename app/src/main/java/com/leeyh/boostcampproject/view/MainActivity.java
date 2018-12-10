package com.leeyh.boostcampproject.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.adapter.MovieRecyclerAdapter;
import com.leeyh.boostcampproject.databinding.ActivityBindingMainBinding;
import com.leeyh.boostcampproject.viewmodel.MainActivityViewModel;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    ActivityBindingMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_binding_main);

        MainActivityViewModel.Factory factory = new MainActivityViewModel.Factory(getApplicationContext());
        final MainActivityViewModel handler = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
        mBinding.setMainHandler(handler);

        MovieRecyclerAdapter recyclerAdapter = new MovieRecyclerAdapter();
        mBinding.movieResultRecyclerView.setAdapter(recyclerAdapter);
        mBinding.movieResultRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        handler.getIsExistData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (!aBoolean) {
                    mBinding.noResultTextView.setVisibility(View.VISIBLE);
                } else {
                    mBinding.noResultTextView.setVisibility(GONE);
                }
            }
        });

        handler.getIsNetworking().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    mBinding.getRoot().setAlpha(0.6f);

                } else {
                    mBinding.progressBar.setVisibility(GONE);
                    mBinding.getRoot().setAlpha(1.0f);
                }
            }
        });

        mBinding.setListItems(handler.getLiveMovieList());
        mBinding.setTotal(handler.getMovieListTotal());
        mBinding.setStart(handler.getStartPoint());
        mBinding.setDisplay(handler.getDisplayCount());
    }
}
