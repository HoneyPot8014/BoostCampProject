package com.leeyh.boostcampproject.bindadapter;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.adapter.MovieRecyclerAdapter;
import com.leeyh.boostcampproject.helper.Network;
import com.leeyh.boostcampproject.model.MovieModel;

import java.io.IOException;

import static com.leeyh.boostcampproject.constant.StaticString.CACHE_SIZE;

public class BindAdapter {

    static LruCache<String, Bitmap> LRU_CACHE = new LruCache<>(CACHE_SIZE);

    @BindingAdapter({"setItems"})
    public static void bindItem(RecyclerView recyclerView, ObservableArrayList<MovieModel> items) {
        MovieRecyclerAdapter adapter = (MovieRecyclerAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    @BindingAdapter({"loadImage"})
    public static void loadImage(ImageView imageView, String url) {
        if (LRU_CACHE.get(url) == null) {
            new AsyncLoadImage(imageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            imageView.setImageBitmap(LRU_CACHE.get(url));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @BindingAdapter({"scrolled"})
    public static void requestWhenScrollRecyclerView(RecyclerView recyclerView, View.OnScrollChangeListener listener) {
        recyclerView.setOnScrollChangeListener(listener);
//        recyclerView.setOnScrollListener(listener);
    }


    private static class AsyncLoadImage extends AsyncTask<String, Void, Bitmap> {

        @SuppressLint("StaticFieldLeak")
        private ImageView mImageView;

        AsyncLoadImage(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Network network = new Network();
            Bitmap bitmap = null;
            try {
                bitmap = network.loadImage(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                LRU_CACHE.put(strings[0], bitmap);
                return bitmap;
            } else {
                return bitmap;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) {
                mImageView.setImageResource(R.drawable.no_image);
            } else {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }
}
