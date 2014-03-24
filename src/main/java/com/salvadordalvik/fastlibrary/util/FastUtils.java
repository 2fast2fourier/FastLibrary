package com.salvadordalvik.fastlibrary.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.List;

/**
 * binfeed
 * Created by Matthew Shepard on 11/17/13.
 */
public class FastUtils {

    private static int id = 99999;
    public static synchronized int getSimpleUID(){
        id++;
        return id;
    }

    public static void startUrlIntent(Activity parent, String url){
        if(parent != null && url != null){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            PackageManager pacman = parent.getPackageManager();
            List<ResolveInfo> res = pacman.queryIntentActivities(browserIntent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (res.size() > 0) {
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                parent.startActivity(browserIntent);
            } else {
                String[] split = url.split(":");
                Toast.makeText(
                        parent,
                        "No application found for protocol" + (split.length > 0 ? ": " + split[0] : "."),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public static int safeParseInt(String integer){
        return safeParseInt(integer, 0);
    }
    public static int safeParseInt(String integer, int fallback) {
        try{
            return Integer.parseInt(integer);
        }catch (Exception e){
            e.printStackTrace();
        }
        return fallback;
    }

    public static void showSimpleShareChooser(Context context, String title, String message, String chooserTitle){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, chooserTitle));
    }

    public static float calculateScrollDistance(Activity activity, float inches){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (inches*160f)/(metrics.heightPixels/metrics.density);
    }

    public static boolean isKeyEnter(KeyEvent event) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            return isKeyEnterAPI11(event);
        }else{
            return isKeyEnterCompat(event);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static boolean isKeyEnterAPI11(KeyEvent event){
        return event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER);
    }

    private static boolean isKeyEnterCompat(KeyEvent event){
        return event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER);
    }

    public static String getSafeString(Fragment fragment, int stringRes) {
        Activity act = fragment.getActivity();
        if(act != null){
            return act.getString(stringRes);
        }
        return "";
    }

    public static void copyToClipboard(Context context, String label, String text) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            copyToClipboardCompatAPI11(context, label, text);
        }else{
            copyToClipboardCompat(context, label, text);
        }
    }

    private static void copyToClipboardCompat(Context context, String label, String text){
        android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setText(text);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void copyToClipboardCompatAPI11(Context context, String label, String text){
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(ClipData.newPlainText(label, text));
    }
}
