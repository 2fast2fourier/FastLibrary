package com.salvadordalvik.fastlibrary.list;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public interface FastItem {
    public int getId();
    public int getType();
    public int getLayoutId();
    public boolean isEnabled();
    public void updateView(View view, Object holder);
    public Object generateViewHolder(View view);
    public void onItemClick(Activity act, Fragment fragment);
    public void setType(int type);
}
