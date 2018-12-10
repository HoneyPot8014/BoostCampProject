package com.leeyh.boostcampproject.repository;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.leeyh.boostcampproject.helper.Cache;
import com.leeyh.boostcampproject.helper.Network;
import com.leeyh.boostcampproject.helper.ResponseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.leeyh.boostcampproject.constant.StaticString.IMAGE;
import static com.leeyh.boostcampproject.constant.StaticString.ITEMS;
import static com.leeyh.boostcampproject.constant.StaticString.QUERY;
import static com.leeyh.boostcampproject.constant.StaticString.START;

public class MovieDataRepository {

    private OnNetworkExceptionOccurred mNetworkExceptionListener;
    private OnNetworkStatusListener mNetworkStatusListener;

    public MovieDataRepository(OnNetworkExceptionOccurred networkExceptionListener, OnNetworkStatusListener networkStatusListener) {
        this.mNetworkExceptionListener = networkExceptionListener;
        this.mNetworkStatusListener = networkStatusListener;
    }

    public MovieDataRepository(OnNetworkExceptionOccurred networkExceptionListener) {
        this.mNetworkExceptionListener = networkExceptionListener;
    }

    public String getMovieList(String query, String start) throws ExecutionException, InterruptedException {
        return new AsyncRequest(mNetworkExceptionListener, mNetworkStatusListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query, start).get();
    }

    public Bitmap loadImage(String url) throws ExecutionException, InterruptedException {
        return new AsyncLoadImage(mNetworkExceptionListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
    }

    private static class AsyncRequest extends AsyncTask<String, Exception, String> {

        private Network mNetwork;
        private OnNetworkExceptionOccurred mNetworkExceptionListener;
        private OnNetworkStatusListener mNetworkStatusListener;

        AsyncRequest(OnNetworkExceptionOccurred networkListener, OnNetworkStatusListener workingListener) {
            this.mNetwork = new Network();
            this.mNetworkExceptionListener = networkListener;
            this.mNetworkStatusListener = workingListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            super.onProgressUpdate(values);
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
            super.onPostExecute(s);
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
