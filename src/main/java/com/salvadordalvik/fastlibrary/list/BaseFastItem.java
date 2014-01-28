package com.salvadordalvik.fastlibrary.list;

import android.view.View;
import com.salvadordalvik.fastlibrary.util.FastUtils;

/**
 * binfeed
 * Created by Matthew Shepard on 11/17/13.
 */
public abstract class BaseFastItem<T> implements FastItem {
    protected int id, type, layoutId;
    protected boolean enabled;

    public BaseFastItem(int layoutId) {
        this(layoutId, FastUtils.getSimpleUID(), true);
    }

    public BaseFastItem(int layoutId, int id) {
        this(layoutId, id, true);
    }

    public BaseFastItem(int layoutId, int id, boolean enabled) {
        this.id = id;
        this.type = 0;
        this.layoutId = layoutId;
        this.enabled = enabled;
    }

    public abstract T createViewHolder(View view);
    public abstract void updateViewFromHolder(View view, T holder);

    @Override
    public void updateView(View view, Object holder) {
        updateViewFromHolder(view, (T)holder);
    }

    @Override
    public Object generateViewHolder(View view) {
        return createViewHolder(view);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getLayoutId() {
        return layoutId;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }
}
