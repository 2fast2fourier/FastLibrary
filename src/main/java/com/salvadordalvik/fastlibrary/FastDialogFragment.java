package com.salvadordalvik.fastlibrary;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.salvadordalvik.fastlibrary.request.FastRequest;
import com.salvadordalvik.fastlibrary.request.FastVolley;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public abstract class FastDialogFragment extends DialogFragment implements FastRequest.FastStatusCallback, OnRefreshListener {
    private int layoutId, titleRes;

    public FastDialogFragment(int layoutId) {
        super();
        this.layoutId = layoutId;
    }

    public FastDialogFragment(int layoutId, int titleRes) {
        super();
        this.layoutId = layoutId;
        this.titleRes = titleRes;
    }

    public abstract void viewCreated(View frag, Bundle savedInstanceState);
    public abstract void refreshData(boolean pullToRefresh);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if(titleRes > 0){
            dialog.setTitle(titleRes);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frag = inflater.inflate(layoutId, container, false);
        return frag;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewCreated(view, savedInstanceState);
    }

    protected void queueRequest(FastRequest request){
        FastVolley.queueRequest(request, this);
    }

    @Override
    public void onSuccess(FastRequest request) {
    }

    @Override
    public void onFailure(FastRequest request, VolleyError error) {
    }

    @Override
    public void onRefreshStarted(View view) {
        refreshData(true);
    }

    public void invalidateOptionsMenu() {
        FragmentActivity activity = getActivity();
        if(activity != null){
            activity.supportInvalidateOptionsMenu();
        }
    }

    protected void setTitle(int titleRes) {
        setTitle(getString(titleRes));
    }

    protected void setTitle(String title) {
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.setTitle(title);
        }
    }
}
