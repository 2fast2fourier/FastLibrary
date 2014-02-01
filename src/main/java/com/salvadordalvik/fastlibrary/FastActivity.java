package com.salvadordalvik.fastlibrary;

import android.app.Activity;
import android.os.Bundle;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public class FastActivity extends Activity {
    private final int layoutId;
    public FastActivity(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
    }
}
