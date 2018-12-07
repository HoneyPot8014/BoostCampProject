package com.leeyh.boostcampproject.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.adapter.MovieRecyclerAdapter;
import com.leeyh.boostcampproject.databinding.ActivityBindingMainBinding;
import com.leeyh.boostcampproject.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    ActivityBindingMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_binding_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_binding_main);
        MainActivityViewModel handler = new MainActivityViewModel();
        mBinding.setMainHandler(handler);
        MovieRecyclerAdapter recyclerAdapter = new MovieRecyclerAdapter();
        mBinding.movieResultRecyclerView.setAdapter(recyclerAdapter);
        mBinding.movieResultRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }
}
