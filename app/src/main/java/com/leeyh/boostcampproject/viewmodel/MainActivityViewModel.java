package com.leeyh.boostcampproject.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.adapter.MovieRecyclerAdapter;
import com.leeyh.boostcampproject.helper.ExceptionHandle;
import com.leeyh.boostcampproject.helper.Network;
import com.leeyh.boostcampproject.model.MovieModel;
import com.leeyh.boostcampproject.repository.MovieDataRepository;
import com.leeyh.boostcampproject.repository.OnNetworkExceptionOccurred;
import com.leeyh.boostcampproject.repository.OnNetworkStatusListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.leeyh.boostcampproject.constant.StaticString.DEFAULT_START;
import static com.leeyh.boostcampproject.constant.StaticString.DISPLAY;
import static com.leeyh.boostcampproject.constant.StaticString.ITEMS;
import static com.leeyh.boostcampproject.constant.StaticString.START;
import static com.leeyh.boostcampproject.constant.StaticString.TOTAL;

public class MainActivityViewModel extends ViewModel {

    //mEditTextGetText two way binding but, not work in liveData
    //it works on ObservableField not bad
    public ObservableField<String> mEditTextGetText;
    private MutableLiveData<ArrayList<MovieModel>> mLiveMovieList;
    private MutableLiveData<Integer> mMovieListTotal;
    private MutableLiveData<Integer> mStartPoint;
    private MutableLiveData<Integer> mDisplayCount;
    private MovieDataRepository mRepository;
    private MutableLiveData<Boolean> mIsExistData;
    private MutableLiveData<Boolean> mIsNetworking;
    private ArrayList<MovieModel> mMovieDataList;
    private String requestedQuery;
    private Network mNetwork;
    private Gson gson;
    private boolean shouldShowEndData;

    public MainActivityViewModel(final Context context) {

        this.mLiveMovieList = new MutableLiveData<>();
        this.mMovieListTotal = new MutableLiveData<>();
        this.mStartPoint = new MutableLiveData<>();
        this.mDisplayCount = new MutableLiveData<>();
        this.mEditTextGetText = new ObservableField<>();
        this.mIsNetworking = new MutableLiveData<>();
        this.mMovieDataList = new ArrayList<>();
        this.mIsExistData = new MutableLiveData<>();
        this.mNetwork = new Network();
        this.gson = new Gson();
        this.requestedQuery = "";
        this.shouldShowEndData = true;
        this.mRepository = new MovieDataRepository(new OnNetworkExceptionOccurred() {
            @Override
            public void onHandleError(Exception e) {
                ExceptionHandle.handleError(context, e);
            }
        }, new OnNetworkStatusListener() {

            @Override
            public void onNetworkStart() {
                mIsNetworking.setValue(true);
            }

            @Override
            public void onNetworkFinished() {
                mIsNetworking.setValue(false);
            }
        });
        mEditTextGetText.set("");
        mLiveMovieList.setValue(mMovieDataList);
        mIsExistData.setValue(true);
        mIsNetworking.setValue(false);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private Context mContext;

        public Factory(@NonNull Context context) {
            mContext = context;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> viewModelClass) {
            return (T) new MainActivityViewModel(mContext);
        }
    }

    public RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
            if (!recyclerView.canScrollVertically(1)) {
                //if recyclerView focus on bottom item handle
                MovieRecyclerAdapter adapter = (MovieRecyclerAdapter) recyclerView.getAdapter();
                if (adapter.getItemCount() < adapter.getTotalSize()) {
                    if (mNetwork.networkStatus(recyclerView.getContext())) {
                        try {
                            String response = mRepository.getMovieList(requestedQuery, String.valueOf(adapter.getStartPoint() + 10));
                            onResponseParsing(response);
                        } catch (Exception e) {
                            ExceptionHandle.handleError(recyclerView.getContext(), e);
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (shouldShowEndData) {
                        Toast.makeText(recyclerView.getContext(), R.string.end_data, Toast.LENGTH_SHORT).show();
                        shouldShowEndData = !shouldShowEndData;
                    }
                }
            }
        }
    };

    public void onSearchBtnClicked(final View view) {
        InputMethodManager mInputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (mNetwork.networkStatus(view.getContext())) {
            if (mEditTextGetText == null) {
                Toast.makeText(view.getContext(), R.string.can_not_research, Toast.LENGTH_SHORT).show();
                return;
            }
            String blankCheckString = mEditTextGetText.get().trim();
            if (blankCheckString.length() == 0) {
                Toast.makeText(view.getContext(), R.string.can_not_research, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mEditTextGetText.equals(requestedQuery)) {
                mIsNetworking.setValue(true);
                mMovieDataList.clear();
                try {
                    String response = mRepository.getMovieList(mEditTextGetText.get(), DEFAULT_START);
                    this.mIsExistData.setValue(true);
                    onResponseParsing(response);
//                    requestedQuery = mEditTextGetText.getValue();
                    requestedQuery = mEditTextGetText.get();
//                    requestedQuery = mEditTextGetText;
                    shouldShowEndData = true;
                } catch (Exception e) {
                    ExceptionHandle.handleError(view.getContext(), e);
                }
            }
        } else {
            Toast.makeText(view.getContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
        }
    }

    public synchronized void onEditTextClicked(View view) {
        if (mEditTextGetText.get() != null) {
            if (mEditTextGetText.get().length() != 0) {
                mEditTextGetText.set("");
            }
        }
    }

    private void onResponseParsing(String response) throws JSONException {
        JSONObject responseJSON = new JSONObject(response);
        mMovieListTotal.setValue(responseJSON.getInt(TOTAL));
        mStartPoint.setValue(responseJSON.getInt(START));
        int displayCount = responseJSON.getInt(DISPLAY);
        mDisplayCount.setValue(displayCount);
        JSONArray items = responseJSON.getJSONArray(ITEMS);
        if (items.length() == 0) {
            mIsExistData.setValue(false);
            mIsNetworking.setValue(false);
            return;
        }
        mIsExistData.setValue(true);
        for (int i = 0; i < displayCount; i++) {
            MovieModel movieData = gson.fromJson(items.get(i).toString(), MovieModel.class);
            if (mMovieDataList.contains(movieData)) {
                return;
            }
            mMovieDataList.add(movieData);
        }
        mLiveMovieList.setValue(mMovieDataList);
    }

    public MutableLiveData<ArrayList<MovieModel>> getLiveMovieList() {
        return mLiveMovieList;
    }

    public MutableLiveData<Integer> getMovieListTotal() {
        return mMovieListTotal;
    }

    public MutableLiveData<Integer> getStartPoint() {
        return mStartPoint;
    }

    public MutableLiveData<Integer> getDisplayCount() {
        return mDisplayCount;
    }

    public MutableLiveData<Boolean> getIsExistData() {
        return mIsExistData;
    }

    public MutableLiveData<Boolean> getIsNetworking() {
        return mIsNetworking;
    }
}
