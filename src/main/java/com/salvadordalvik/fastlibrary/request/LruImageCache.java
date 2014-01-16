package com.salvadordalvik.fastlibrary.request;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public class LruImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache{
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1){
            return sizeOfAPI12(value);
        }
        return value.getRowBytes()*value.getHeight();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private int sizeOfAPI12(Bitmap value){
        return value.getByteCount();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
