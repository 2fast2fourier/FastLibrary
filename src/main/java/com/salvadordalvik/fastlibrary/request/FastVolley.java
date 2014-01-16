package com.salvadordalvik.fastlibrary.request;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public class FastVolley {

    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;
    private static LruImageCache imageCache;

    public static synchronized void init(Context context){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        imageCache = new LruImageCache(5242880);
        imageLoader = new ImageLoader(requestQueue, imageCache);
    }

    public static void queueRequest(FastRequest request, FastRequest.FastStatusCallback callback){
        queueRequest(request.build(callback));
    }

    public static void queueRequest(Request request){
        getRequestQueue().add(request);
    }

    public static synchronized ImageLoader getImageLoader(){
        if(requestQueue == null){
            throw new RuntimeException("Image Loader has not been initialized, CALL FastVolley.init() BEFORE USE");
        }
        return imageLoader;
    }

    public static void clearImageCache(){
        if(imageCache != null){
            imageCache.evictAll();
        }
    }

    public static synchronized void invalidateCache(String url, boolean hard){
        if(requestQueue != null){
            requestQueue.getCache().invalidate(url, hard);
        }
    }

    private static synchronized RequestQueue getRequestQueue(){
        if(requestQueue == null){
            throw new RuntimeException("Request Queue has not been initialized, CALL FastVolley.init() BEFORE USE");
        }
        return requestQueue;
    }
}
