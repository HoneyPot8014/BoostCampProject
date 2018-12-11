package com.leeyh.boostcampproject.bindadapter;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leeyh.boostcampproject.R;
import com.leeyh.boostcampproject.adapter.MovieRecyclerAdapter;
import com.leeyh.boostcampproject.helper.Cache;
import com.leeyh.boostcampproject.helper.ExceptionHandle;
import com.leeyh.boostcampproject.helper.Network;
import com.leeyh.boostcampproject.model.MovieModel;
import com.leeyh.boostcampproject.repository.MovieDataRepository;
import com.leeyh.boostcampproject.repository.OnNetworkExceptionOccurred;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class BindAdapter {

    @BindingAdapter({"setItems"})
    public static void bindItem(RecyclerView recyclerView, MutableLiveData<ArrayList<MovieModel>> items) {
        final MovieRecyclerAdapter adapter = (MovieRecyclerAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            items.observeForever(new Observer<ArrayList<MovieModel>>() {
                @Override
                public void onChanged(@Nullable ArrayList<MovieModel> movieModels) {
                    adapter.setItems(movieModels);
                }
            });
        }
    }

    @BindingAdapter({"loadImage"})
    public static void loadImage(final ImageView imageView, String url) {
        LruCache<String, Bitmap> cache = Cache.getCache();
        Network network = new Network();
        MovieDataRepository movieDataRepository = new MovieDataRepository(new OnNetworkExceptionOccurred() {
            @Override
            public void onHandleError(Exception e) {
                ExceptionHandle.handleError(imageView.getContext(), e);
            }
        });
        if (url == null || url.equals("")) {
            imageView.setImageResource(R.drawable.no_image);
            return;
        }
        if (cache.get(url) == null) {
            if (network.networkStatus(imageView.getContext())) {
                try {
                    Bitmap bitmap = movieDataRepository.loadImage(url);
                    if (bitmap != null) {
                        cache.put(url, bitmap);
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(imageView.getContext(), R.string.failed_load_image, Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(imageView.getContext(), R.string.failed_load_image, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(imageView.getContext(), R.string.load_image_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            imageView.setImageBitmap(cache.get(url));
        }
    }

    @BindingAdapter({"scrolled"})
    public static void requestWhenScrollRecyclerView(RecyclerView recyclerView, RecyclerView.OnScrollListener listener) {
        recyclerView.addOnScrollListener(listener);
    }

    @BindingAdapter({"parsingText"})
    public static void setText(TextView textView, String rowData) {
        String parsedString = rowData.replace("<b>", "").replace("</b>", "");
        textView.setText(parsedString);
    }

    @BindingAdapter({"searchTextClicked"})
    public static void setTouchListener(EditText editText, View.OnTouchListener listener) {
        editText.setOnTouchListener(listener);
    }
}
