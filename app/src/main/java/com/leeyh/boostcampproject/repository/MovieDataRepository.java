package com.leeyh.boostcampproject.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.leeyh.boostcampproject.helper.Cache;
import com.leeyh.boostcampproject.helper.Network;
import com.leeyh.boostcampproject.helper.ResponseException;
import com.leeyh.boostcampproject.model.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leeyh.boostcampproject.constant.StaticString.IMAGE;
import static com.leeyh.boostcampproject.constant.StaticString.ITEMS;
import static com.leeyh.boostcampproject.constant.StaticString.QUERY;
import static com.leeyh.boostcampproject.constant.StaticString.START;
import static com.leeyh.boostcampproject.constant.StaticString.TOTAL;

public class MovieDataRepository {

    private OnNetworkExceptionOccurred mNetworkExceptionListener;
    private OnNetworkStatusListener mNetworkStatusListener;
    private Gson mGson;
    private String mQuery;
    private int mTotal;
    private int mStart;

    public MovieDataRepository(OnNetworkExceptionOccurred networkExceptionListener, OnNetworkStatusListener networkStatusListener) {
        this.mNetworkExceptionListener = networkExceptionListener;
        this.mNetworkStatusListener = networkStatusListener;
        this.mGson = new Gson();
    }

    public MovieDataRepository(OnNetworkExceptionOccurred networkExceptionListener) {
        this.mNetworkExceptionListener = networkExceptionListener;
        this.mGson = new Gson();
    }

    public ArrayList<MovieModel> getMovieList(String query, String start, Context context) throws ExecutionException, InterruptedException, JSONException {
        String response = new AsyncRequest(mNetworkExceptionListener, mNetworkStatusListener, context).execute(query, start).get();
        ArrayList<MovieModel> items = new ArrayList<>();
        JSONObject parsedResponse = new JSONObject(response);
        JSONArray responseItems = parsedResponse.getJSONArray(ITEMS);
        this.mQuery = query;
        this.mTotal = parsedResponse.getInt(TOTAL);
        this.mStart = parsedResponse.getInt(START);
        for (int i = 0; i < responseItems.length(); i++) {
            MovieModel item = mGson.fromJson(responseItems.getJSONObject(i).toString(), MovieModel.class);
            items.add(item);
        }
        return items;
    }

    public Bitmap loadImage(String url) throws ExecutionException, InterruptedException {
        return new AsyncLoadImage(mNetworkExceptionListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
    }

    public String getQuery() {
        return mQuery;
    }

    public int getTotal() {
        return mTotal;
    }

    public int getStart() {
        return mStart;
    }

    private static class AsyncRequest extends AsyncTask<String, Exception, String> {

        private Network mNetwork;
        private OnNetworkExceptionOccurred mNetworkExceptionListener;
        private OnNetworkStatusListener mNetworkStatusListener;
        private ProgressDialog asyncDialog;

        AsyncRequest(OnNetworkExceptionOccurred networkListener, OnNetworkStatusListener workingListener, Context context) {
            this.mNetwork = new Network();
            this.mNetworkExceptionListener = networkListener;
            this.mNetworkStatusListener = workingListener;
            this.asyncDialog = new ProgressDialog(context);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.show();
            mNetworkStatusListener.onNetworkStart();
        }

        @Override
        protected String doInBackground(String... query) {
            Map<String, String> queryString = new HashMap<>();
            try {
                queryString.put(QUERY, URLEncoder.encode(query[0], "utf-8"));
                queryString.put(START, String.valueOf(query[1]));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String result = null;
            try {
                result = mNetwork.request(queryString);
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress(e);
            } catch (ResponseException e) {
                e.printStackTrace();
                publishProgress(e);
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Exception... values) {
            handleError(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                mNetwork.reader.close();
                mNetwork.urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mNetworkStatusListener.onNetworkFinished();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject receivedJSON = new JSONObject(s);
                JSONArray items = receivedJSON.getJSONArray(ITEMS);
                for (int i = 0; i < items.length(); i++) {
                    String image = ((JSONObject) items.get(i)).getString(IMAGE);
                    new MovieDataRepository(mNetworkExceptionListener, mNetworkStatusListener).loadImage(image);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mNetworkStatusListener.onNetworkFinished();
            asyncDialog.dismiss();
        }

        private void handleError(Exception e) {
            if (mNetworkExceptionListener != null) {
                mNetworkExceptionListener.onHandleError(e);
            }
        }
    }

    private static class AsyncLoadImage extends AsyncTask<String, Exception, Bitmap> {

        private Network mNetwork;
        private OnNetworkExceptionOccurred mNetworkExceptionListener;

        AsyncLoadImage(OnNetworkExceptionOccurred networkListener) {
            this.mNetwork = new Network();
            this.mNetworkExceptionListener = networkListener;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            if (strings[0] != null && !strings[0].equals("")) {
                try {
                    bitmap = mNetwork.loadImage(strings[0]);
                    if (bitmap != null) {
                        Cache.getCache().put(strings[0], bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress(e);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    publishProgress(e);
                }
            }
            if (bitmap != null) {
                return bitmap;
            } else {
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Exception... values) {
            super.onProgressUpdate(values);
            handleError(values[0]);
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
            try {
                mNetwork.reader.close();
                mNetwork.urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleError(Exception e) {
            if (mNetworkExceptionListener != null) {
                mNetworkExceptionListener.onHandleError(e);
            }
        }
    }
}
