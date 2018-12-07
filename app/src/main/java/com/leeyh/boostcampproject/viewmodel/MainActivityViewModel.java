package com.leeyh.boostcampproject.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.helper.Network;
import com.leeyh.boostcampproject.model.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.leeyh.boostcampproject.constant.StaticString.DEFAULT_DISPLAY;
import static com.leeyh.boostcampproject.constant.StaticString.DEFAULT_START;
import static com.leeyh.boostcampproject.constant.StaticString.DISPLAY;
import static com.leeyh.boostcampproject.constant.StaticString.QUERY;
import static com.leeyh.boostcampproject.constant.StaticString.START;

public class MainActivityViewModel extends BaseObservable {

    public ObservableArrayList<MovieModel> movieDataList = new ObservableArrayList<>();
    public ObservableField<String> movieInfo = new ObservableField<>();
    private Network network = new Network();
    private String requestedMovieInfo = "";
    private int onRecyclerViewScrollEndCount = 0;
    public View.OnScrollChangeListener listener = new View.OnScrollChangeListener() {
        @Override
        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
            if (!view.canScrollVertically(1)) {
                if (network.networkStatus(view.getContext())) {
                    if(DEFAULT_START + 10 * onRecyclerViewScrollEndCount <= 1000) {
                        AsyncRequest request = new AsyncRequest(movieDataList, network
                                , DEFAULT_DISPLAY,DEFAULT_START + 10 * onRecyclerViewScrollEndCount);
                        request.execute(requestedMovieInfo);
                        onRecyclerViewScrollEndCount += 1;
                    }
                }
            }
        }
    };

    public void onSearchBtnClicked(View view) {
        if (network.networkStatus(view.getContext())) {
            String blankCheckString = movieInfo.get().replace(" ","");
            if(blankCheckString.equals("") || movieInfo.get().equals(" ")) {
                Toast.makeText(view.getContext(), R.string.can_not_research, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!movieInfo.get().equals(requestedMovieInfo)) {
                movieDataList.clear();
                AsyncRequest request = new AsyncRequest(movieDataList, network, DEFAULT_DISPLAY, DEFAULT_START);
                request.execute(movieInfo.get());
                requestedMovieInfo = movieInfo.get();
                onRecyclerViewScrollEndCount = 1;
            }
        } else {
            Toast.makeText(view.getContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
        }
    }

    public void onEditTextClicked(View v) {
        if(movieInfo.get() != null) {
            if(movieInfo.get().length() != 0) {
                movieInfo.set("");
            }
        }
    }


    private static class AsyncRequest extends AsyncTask<String, Void, String> {

        private List<MovieModel> movieDataList;
        private Network mNetwork;
        private int mDisplay;
        private int mStart;

        AsyncRequest(List<MovieModel> movieDataList, Network network, int display, int start) {
            this.movieDataList = movieDataList;
            this.mNetwork = network;
            this.mDisplay = display;
            this.mStart = start;
        }

        @Override
        protected String doInBackground(String... query) {
            Map<String, String> queryString = new HashMap<>();
            try {
                queryString.put(QUERY, URLEncoder.encode(query[0], "utf-8"));
                queryString.put(DISPLAY, String.valueOf(mDisplay));
                queryString.put(START, String.valueOf(mStart));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String result = null;
            try {
                result = mNetwork.request(queryString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject response = new JSONObject(s);
                JSONArray jsonArray = response.getJSONArray("items");
                Gson gson = new Gson();
                for (int i = 0; i < Integer.parseInt(response.getString("total")); i++) {
                    MovieModel movieData = gson.fromJson(jsonArray.get(i).toString(), MovieModel.class);
                    if(movieDataList.contains(movieData)) {
                        return;
                    }
                    movieDataList.add(movieData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }
}
