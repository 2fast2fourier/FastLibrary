package com.salvadordalvik.fastlibrary.alert;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.salvadordalvik.fastlibrary.R;
import com.salvadordalvik.fastlibrary.util.FastUtils;

/**
 * Created by matthewshepard on 1/31/14.
 */
public class FastAlert {
    private static final int DEFAULT_TIMEOUT = 3000;

    private static PopupWindow currentAlert;
    private static final Handler handler = new Handler();
    private static final Runnable popupCloseRunner = new Runnable() {
        @Override
        public void run() {
            try{
                if(currentAlert != null){
                    currentAlert.dismiss();
                    currentAlert = null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public static void notice(Fragment fragment, int messageRes){
        notice(fragment.getActivity(), fragment.getView(), FastUtils.getSafeString(fragment, messageRes), R.drawable.ic_action_about);
    }

    public static void notice(Fragment fragment, String message){
        notice(fragment.getActivity(), fragment.getView(), message, R.drawable.ic_action_about);
    }

    public static void notice(Context context, View parentView, String message){
        notice(context, parentView, message, R.drawable.ic_action_about);
    }

    public static void notice(Context context, View parentView, String message, int iconRes){
        displayAlert(context, parentView, message, null, DEFAULT_TIMEOUT, iconRes, null);
    }

    public static void process(Context context, View parentView, String message){
        if(context != null && !TextUtils.isEmpty(message) && parentView != null){
            RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(1000);
            anim.setRepeatCount(Animation.INFINITE);
            displayAlert(context, parentView, message, null, DEFAULT_TIMEOUT, R.drawable.ic_action_refresh, anim);
        }
    }

    /**
     * Displays loading alert, this alert will not automatically close itself.
     * Alert must be closed by calling dismiss() on the returned PopupWindow.
     * Any future calls to display alerts will dismiss this alert to replace it.
     * @param context
     * @param parentView The main view for the current fragment or activity. Required by PopupWindow.
     * @param message Message to display.
     * @return A PopupWindow instance without a timeout specified.
     */
    public static PopupWindow loadingIndeterminate(Context context, View parentView, String message){
        if(context != null && !TextUtils.isEmpty(message) && parentView != null){
            RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(1000);
            anim.setRepeatCount(Animation.INFINITE);
            return displayAlert(context, parentView, message, null, -1, R.drawable.ic_action_refresh, anim);
        }
        return null;
    }

    public static void error(Fragment fragment, int messageRes){
        error(fragment.getActivity(), fragment.getView(), FastUtils.getSafeString(fragment, messageRes), R.drawable.ic_action_error);
    }

    public static void error(Fragment fragment, String message){
        error(fragment.getActivity(), fragment.getView(), message, R.drawable.ic_action_error);
    }

    public static void error(Context context, View parentView, String message){
        error(context, parentView, message, R.drawable.ic_action_error);
    }

    public static void error(Context context, View parentView, String message, int iconRes){
        if(context != null && !TextUtils.isEmpty(message) && parentView != null){
            AlphaAnimation anim = new AlphaAnimation(0.5f, 1.0f);
            anim.setDuration(600);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            displayAlert(context, parentView, message, null, DEFAULT_TIMEOUT, iconRes, anim);
        }
    }

    public static void custom(Context context, View parentView, String message, String submessage, int iconRes){
        displayAlert(context, parentView, message, submessage, DEFAULT_TIMEOUT, iconRes, null);
    }

    public static void custom(Context context, View parentView, String message, String submessage, int iconRes, int timeout, Animation animation){
        displayAlert(context, parentView, message, submessage, timeout, iconRes, animation);
    }

    private synchronized static PopupWindow displayAlert(Context context, View parent, String title, String subtitle, int timeout, int icon, Animation animation){
        if(context == null || parent == null || title == null || parent.getWindowToken() == null){
            return null;
        }
        if(currentAlert != null){
            currentAlert.dismiss();
            currentAlert = null;
            handler.removeCallbacks(popupCloseRunner);
        }
        View popup = LayoutInflater.from(context).inflate(R.layout.alert_popup, null);
        TextView titleView = (TextView) popup.findViewById(R.id.popup_title);
        TextView subtitleView = (TextView) popup.findViewById(R.id.popup_subtitle);
        ImageView iconView = (ImageView) popup.findViewById(R.id.popup_icon);
        titleView.setText(title);
        if(TextUtils.isEmpty(subtitle)){
            subtitleView.setVisibility(View.GONE);
        }else{
            subtitleView.setText(subtitle);
            subtitleView.setVisibility(View.VISIBLE);
        }
        if(icon != 0){
            iconView.setImageResource(icon);
            if(animation != null){
                iconView.startAnimation(animation);
            }
        }
        int popupDimen = (int) context.getResources().getDimension(R.dimen.popup_size);
        currentAlert = new PopupWindow(popup, popupDimen, popupDimen);
        currentAlert.setBackgroundDrawable(null);
        currentAlert.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                currentAlert = null;
                handler.removeCallbacks(popupCloseRunner);
            }
        });
        currentAlert.showAtLocation(parent, Gravity.CENTER, 0, 0);
        if(timeout > 0){
            handler.postDelayed(popupCloseRunner, timeout);
        }
        return currentAlert;
    }
}
