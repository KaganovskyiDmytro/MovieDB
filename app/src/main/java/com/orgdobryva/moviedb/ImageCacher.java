package com.orgdobryva.moviedb;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmytro on 30/03/16.
 */
public class ImageCacher {

    private Map<String, Bitmap> cache = new HashMap<>();

    public Map<String, Bitmap> getCache() {
        return cache;
    }

    public boolean contains(String path) {
        return cache.containsKey(path);
    }

    public Bitmap get(String path) {
        return cache.get(path);
    }

    public void push(String path, Bitmap bitmap) {
        cache.put(path, bitmap);
    }
}
