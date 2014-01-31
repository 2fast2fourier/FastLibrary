package com.salvadordalvik.fastlibrary.widget;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by matthewshepard on 1/30/14.
 */
public class ToggleSlidingPaneLayout extends SlidingPaneLayout {
    private boolean slidable = true;

    public ToggleSlidingPaneLayout(Context context) {
        super(context);
    }

    public ToggleSlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(slidable){
            try{
                return super.onInterceptTouchEvent(ev);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(slidable){
            return super.onTouchEvent(ev);
        }
        return false;
    }

    public void setTouchSlidable(boolean slidable){
        this.slidable = slidable;
    }
}
