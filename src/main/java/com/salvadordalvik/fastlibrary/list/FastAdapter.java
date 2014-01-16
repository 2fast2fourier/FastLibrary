package com.salvadordalvik.fastlibrary.list;

import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public class FastAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Activity act;
    private Fragment frag;
    private ArrayList<FastItem> itemList = new ArrayList<FastItem>();
    private LayoutInflater inflater;

    private boolean allEnabled = true;
    private int typeCount;

    public FastAdapter(Activity activity, Fragment fragment, int typeCount) {
        this.act = activity;
        this.frag = fragment;
        this.typeCount = typeCount;
        inflater = LayoutInflater.from(activity);
    }

    public void addItems(List<? extends FastItem> list){
        itemList.addAll(list);
        for(FastItem item : list){
            allEnabled = allEnabled && item.isEnabled();
        }
        notifyDataSetChanged();
    }

    public void addItems(FastItem... list){
        for(FastItem item : list){
            allEnabled = allEnabled && item.isEnabled();
            itemList.add(item);
        }
        notifyDataSetChanged();
    }

    public void clearList(){
        itemList.clear();
        allEnabled = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public FastItem getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FastItem item = itemList.get(position);
        Object viewHolder;
        if(convertView == null){
            convertView = inflater.inflate(item.getLayoutId(), parent, false);
            viewHolder = item.generateViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = convertView.getTag();
        }
        item.updateView(convertView, viewHolder);
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        itemList.get(position).onItemClick(act, frag);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return allEnabled;
    }

    @Override
    public boolean isEnabled(int position) {
        return itemList.get(position).isEnabled();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return typeCount;
    }

    @Override
    public boolean isEmpty() {
        return itemList.isEmpty();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
