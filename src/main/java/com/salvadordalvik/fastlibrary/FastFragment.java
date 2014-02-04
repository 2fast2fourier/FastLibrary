package com.salvadordalvik.fastlibrary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.salvadordalvik.fastlibrary.request.FastRequest;
import com.salvadordalvik.fastlibrary.request.FastVolley;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public abstract class FastFragment extends Fragment implements FastRequest.FastStatusCallback, OnRefreshListener {
    private int layoutId, menuId;
    private PullToRefreshLayout ptr = null;
    private long lastRefreshTime = 0;

    public FastFragment(int layoutId) {
        this(layoutId, 0);
    }

    public FastFragment(int layoutId, int menuId) {
        super();
        this.layoutId = layoutId;
        this.menuId = menuId;
    }

    public abstract void viewCreated(View frag, Bundle savedInstanceState);
    public abstract void refreshData(boolean pullToRefresh, boolean staleRefresh);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(menuId > 0){
            setHasOptionsMenu(true);
        }
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
        if(view instanceof PullToRefreshLayout){
            ptr = (PullToRefreshLayout) view;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(ptr != null){
            setupPullToRefresh(ptr);
        }
    }

    protected void setupPullToRefresh(PullToRefreshLayout ptr){
        ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener(this).setup(ptr);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(menuId != 0){
            inflater.inflate(menuId, menu);
        }
    }

    public void queueRequest(FastRequest request){
        FastVolley.queueRequest(request, this);
    }

    @Override
    public void onSuccess(FastRequest request) {
        onRefreshCompleted();
    }

    @Override
    public void onFailure(FastRequest request, VolleyError error) {
        onRefreshCompleted();
    }

    public void onRefreshCompleted(){
        if(ptr != null){
            ptr.setRefreshing(false);
        }
    }

    public void startRefresh(){
        startRefresh(false);
    }

    public void startRefresh(boolean staleRequest){
        if(ptr != null){
            ptr.setRefreshing(true);
        }
        lastRefreshTime = System.currentTimeMillis();
        refreshData(false, staleRequest);
    }

    protected boolean startRefreshIfStale() {
        if(lastRefreshTime < System.currentTimeMillis() - 300000){
            startRefresh(true);
            return true;
        }
        return false;
    }

    public void setRefreshAnimation(boolean refreshing){
        if(ptr != null && ptr.isRefreshing() != refreshing){
            ptr.setRefreshing(refreshing);
            if(refreshing){
                setProgress(100);
            }
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        refreshData(true, false);
    }

    public void invalidateOptionsMenu() {
        FragmentActivity activity = getActivity();
        if(activity != null){
            activity.supportInvalidateOptionsMenu();
        }
    }

    protected void setTitle(CharSequence title) {
        Activity act = getActivity();
        if(act != null){
            act.setTitle(title);
        }
    }

    protected void runOnUiThread(Runnable runnable) {
        Activity act = getActivity();
        if(act != null){
            act.runOnUiThread(runnable);
        }
    }

    protected void setProgress(int newProgress) {
        Activity activity = getActivity();
        if(activity != null){
            activity.setProgress(newProgress*100);
        }
    }
}
