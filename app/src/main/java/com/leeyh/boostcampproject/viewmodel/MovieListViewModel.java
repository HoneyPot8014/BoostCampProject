package com.leeyh.boostcampproject.viewmodel;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.helper.ExceptionHandle;
import com.leeyh.boostcampproject.helper.Network;
import com.leeyh.boostcampproject.model.MovieModel;
import com.leeyh.boostcampproject.repository.MovieDataRepository;
import com.leeyh.boostcampproject.repository.OnNetworkExceptionOccurred;
import com.leeyh.boostcampproject.repository.OnNetworkStatusListener;

import java.util.ArrayList;

import static com.leeyh.boostcampproject.constant.StaticString.DEFAULT_START;

public class MovieListViewModel extends ViewModel {

    public MutableLiveData<String> mEditTextGetText;
    public MutableLiveData<ArrayList<MovieModel>> mLiveMovieList;
    public MutableLiveData<Integer> mMovieListSize;
    private MovieDataRepository mRepository;
    private ArrayList<MovieModel> mMovieDataList;
    private Network mNetwork;
    private boolean shouldShowEndData;
    private Dialog progress;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MovieListViewModel(final Context context) {

        this.mLiveMovieList = new MutableLiveData<>();
        this.mEditTextGetText = new MutableLiveData<>();
        this.mMovieListSize = new MutableLiveData<>();
        this.mMovieDataList = new ArrayList<>();
        this.mNetwork = new Network();
        this.shouldShowEndData = true;

        AlertDialog.Builder progressBuilder = new AlertDialog.Builder(context);
        progressBuilder.setView(R.layout.progress);
        progress = progressBuilder.create();

        //register listener to handle error and background working to notify
        this.mRepository = new MovieDataRepository(new OnNetworkExceptionOccurred() {
            @Override
            public void onHandleError(Exception e) {
                ExceptionHandle.handleError(context, e);
            }
        }, new OnNetworkStatusListener() {

            @Override
            public void onNetworkStart() {
                progress.show();
            }

            @Override
            public void onNetworkFinished() {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
        //to handle not exist movieList. before networking and get response, set value
        mLiveMovieList.setValue(mMovieDataList);
        mMovieListSize.setValue(1);
        shouldShowEndData = false;
    }

    public static class MovieViewModelClassFactory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private Context mContext;

        public MovieViewModelClassFactory(@NonNull Context context) {
            mContext = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MovieListViewModel.class)) {
                return (T) new MovieListViewModel(mContext);
            }
            throw new Fragment.InstantiationException("not viewModel class", null);
        }
    }

    private Boolean queryCheck(View view) {
        //not allowed to search with blank
        //to reduce networking, same query is not allowed
        if (mEditTextGetText.getValue() == null) {
            Toast.makeText(view.getContext(), R.string.can_not_research, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String blankCheckString = mEditTextGetText.getValue().trim();
            if (blankCheckString.length() == 0) {
                Toast.makeText(view.getContext(), R.string.can_not_research, Toast.LENGTH_SHORT).show();
                return false;
            } else return !mEditTextGetText.getValue().equals(mRepository.getQuery());
        }
    }

    public void onSearchBtnClicked(final View view) {
        InputMethodManager mInputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (mNetwork.networkStatus(view.getContext())) {
            if (queryCheck(view)) {
                mMovieDataList.clear();
                try {
                    mMovieDataList = mRepository.getMovieList(mEditTextGetText.getValue(), DEFAULT_START);
                    mLiveMovieList.setValue(mMovieDataList);
                    mMovieListSize.setValue(mRepository.getTotal());
                    shouldShowEndData = true;
                } catch (Exception e) {
                    ExceptionHandle.handleError(view.getContext(), e);
                }
            }
        } else {
            Toast.makeText(view.getContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
        }
    }

    public View.OnTouchListener onEditTextTouchedListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (mEditTextGetText.getValue() != null && mEditTextGetText.getValue().length() != 0) {
                    mEditTextGetText.setValue("");
                }
            }
            return false;
        }
    };

    public RecyclerView.OnScrollListener onScrollEndListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
            if (!recyclerView.canScrollVertically(1)) {
                //if recyclerView focus on bottom get items
                if (mMovieDataList.size() < mMovieListSize.getValue()) {
                    if (mNetwork.networkStatus(recyclerView.getContext())) {
                        try {
                            mMovieDataList.addAll(mRepository.getMovieList(mRepository.getQuery(), String.valueOf(mRepository.getStart() + 10)));
                            mLiveMovieList.setValue(mMovieDataList);
                        } catch (Exception e) {
                            ExceptionHandle.handleError(recyclerView.getContext(), e);
                            e.printStackTrace();
                        }
                    }
                } else {
                    //Toast message only one time emit
                    if (shouldShowEndData) {
                        Toast.makeText(recyclerView.getContext(), R.string.end_data, Toast.LENGTH_SHORT).show();
                        shouldShowEndData = !shouldShowEndData;
                    }
                }
            }
        }
    };

    public void onRecyclerItemViewClicked(View view, MovieModel movieModel) {
        Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse(movieModel.getLink()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle animation = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.enter, R.anim.exit).toBundle();
                    view.getContext().startActivity(link, animation);
                } else {
                    view.getContext().startActivity(link);
                }
    }
}
