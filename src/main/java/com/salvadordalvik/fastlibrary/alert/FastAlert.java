package com.salvadordalvik.fastlibrary.alert;

import android.content.Context;
import android.os.Handler;
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
import android.widget.Toast;

import com.salvadordalvik.fastlibrary.R;

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

    public static void notice(Context context, View parentView, String message){
        if(context != null && !TextUtils.isEmpty(message) && parentView != null){
            displayAlert(context, parentView, message, null, DEFAULT_TIMEOUT, android.R.drawable.ic_dialog_info, null);
        }
    }

    public static void process(Context context, View parentView, String message){
        if(context != null && !TextUtils.isEmpty(message) && parentView != null){
            RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(1000);
            anim.setRepeatCount(Animation.INFINITE);
            displayAlert(context, parentView, message, null, DEFAULT_TIMEOUT, android.R.drawable.ic_popup_sync, anim);
        }
    }

    /**
     * Displays loading alert, the alert will not automatically close itself.
     * Alert must be closed by calling dismiss() on the returned PopupWindow.
     * Any future calls to display alerts will dismiss this alert to replace it.
     * @param context
     * @param parentView
     * @param message
     * @return
     */
    public static PopupWindow loadingIndeterminate(Context context, View parentView, String message){
        if(context != null && !TextUtils.isEmpty(message) && parentView != null){
            RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(1000);
            anim.setRepeatCount(Animation.INFINITE);
            return displayAlert(context, parentView, message, null, -1, android.R.drawable.ic_popup_sync, anim);
        }
        throw new RuntimeException("FastAlert: Missing or Invalid Arguments");
    }

    public static void error(Context context, View parentView, String message){
        if(context != null && !TextUtils.isEmpty(message) && parentView != null){
            AlphaAnimation anim = new AlphaAnimation(0.5f, 1.0f);
            anim.setDuration(600);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            displayAlert(context, parentView, message, null, DEFAULT_TIMEOUT, android.R.drawable.ic_dialog_alert, anim);
        }
    }

    private synchronized static PopupWindow displayAlert(Context context, View parent, String title, String subtitle, int timeout, int icon, Animation animation){
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
