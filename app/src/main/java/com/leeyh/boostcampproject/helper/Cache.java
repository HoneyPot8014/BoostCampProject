package com.leeyh.boostcampproject.helper;

import android.graphics.Bitmap;
import android.util.LruCache;

import static com.leeyh.boostcampproject.constant.StaticString.CACHE_SIZE;

public class Cache {

    private static LruCache<String, Bitmap> CACHE;

    public synchronized static LruCache<String, Bitmap> getCache() {
        if (CACHE == null) {
            CACHE = new LruCache<String, Bitmap>(CACHE_SIZE) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
            return CACHE;
        } else {
            return CACHE;
        }
    }
}
