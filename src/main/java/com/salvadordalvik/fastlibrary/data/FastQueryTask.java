package com.salvadordalvik.fastlibrary.data;

import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * FastLib
 * Created by Matthew Shepard on 11/25/13.
 */
public class FastQueryTask<T> extends AsyncTask<String, Void, Cursor> {
    private final QueryResultCallback<T> callback;
    private final FastDatabase database;

    public interface QueryResultCallback<T>{
        public void queryResult(List<T> results);
        public T createItem(Cursor data);
    }

    public FastQueryTask(FastDatabase db, QueryResultCallback<T> callback) {
        this.callback = callback;
        this.database = db;
    }

    @Override
    protected Cursor doInBackground(String... params) {
        return database.query(params);
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        callback.queryResult(createList(cursor));
        cursor.close();
    }

    protected List<T> createList(Cursor data){
        ArrayList<T> items = new ArrayList<T>();
        if(data != null && !data.isClosed() && data.moveToFirst()){
            do{
                items.add(callback.createItem(data));
            }while (data.moveToNext());
        }
        return items;
    }

    public void query(String table, String sort){
        execute(table, sort);
    }

    public void query(String table, String sort, String where){
        execute(table, sort, where);
    }

    public void query(String table, String sort, String where, String... whereargs){
        if(whereargs == null){
            execute(table, sort, where);
        }else{
            String[] args = new String[whereargs.length+3];
            args[0] = table;
            args[1] = sort;
            args[2] = where;
            for(int ix=0;ix<whereargs.length;ix++){
                args[ix+3] = whereargs[ix];
            }
            execute(args);
        }
    }
}
