package com.salvadordalvik.fastlibrary.data;

import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * FastLib
 * Created by Matthew Shepard on 11/25/13.
 */
public class FastQueryTask<T> extends AsyncTask<Void, Void, Cursor> {
    private final QueryResultCallback<T> callback;
    private final FastDatabase database;

    private String table, sort, where;
    private String[] whereArgs;

    public interface QueryResultCallback<T>{
        public int[] findColumns(Cursor data);
        public void queryResult(List<T> results);
        public T createItem(Cursor data, int[] columns);
    }

    public FastQueryTask(FastDatabase db, QueryResultCallback<T> callback) {
        this.callback = callback;
        this.database = db;
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        return database.query(table, sort, where, whereArgs);
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
            int[] columns = callback.findColumns(data);
            do{
                T item = callback.createItem(data, columns);
                if(item != null){
                    items.add(item);
                }
            }while (data.moveToNext());
        }
        return items;
    }

    public void query(String table){
        query(table, null, null);
    }

    public void query(String table, String sort){
        query(table, sort, null);
    }

    public void query(String table, String sort, String where, String... whereargs){
        this.table = table;
        this.sort = sort;
        this.where = where;
        this.whereArgs = whereargs;
        execute();
    }

    public static int[] findColumnIndicies(Cursor cursor, String[] columns){
        int[] index = new int[columns.length];
        for(int ix=0;ix<index.length;ix++){
            index[ix] = cursor.getColumnIndex(columns[ix]);
        }
        return index;
    }
}
