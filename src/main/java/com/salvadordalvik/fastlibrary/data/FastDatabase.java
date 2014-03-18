package com.salvadordalvik.fastlibrary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * binfeed
 * Created by Matthew Shepard on 11/25/13.
 */
public abstract class FastDatabase extends SQLiteOpenHelper {

    public FastDatabase(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public FastDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public int insertRows(String table, List<ContentValues> values){
        return insertRows(table, SQLiteDatabase.CONFLICT_ABORT, values);
    }

    public int insertRows(String table, ContentValues... values){
        return insertRows(table, SQLiteDatabase.CONFLICT_ABORT, values);
    }

    public int insertRows(String table, int conflictAction, ContentValues... values){
        int rows = 0;
        synchronized (this){
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            for(ContentValues value : values){
                if(db.insertWithOnConflict(table, null, value, conflictAction) >= 0){
                    rows++;
                }
            }
            if(rows > 0){
                db.setTransactionSuccessful();
            }
            db.endTransaction();
        }
        return rows;
    }

    public int insertRows(String table, int conflictAction, List<ContentValues> values){
        int rows = 0;
        synchronized (this){
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            for(ContentValues value : values){
                if(db.insertWithOnConflict(table, null, value, conflictAction) >= 0){
                    rows++;
                }
            }
            if(rows > 0){
                db.setTransactionSuccessful();
            }
            db.endTransaction();
        }
        return rows;
    }

    public synchronized Cursor query(String tableOrView, String sort, String where, String... whereArgs){
        Cursor result = getReadableDatabase().query(tableOrView, null, where, whereArgs, null, null, sort);
        return result;
    }

    public synchronized Cursor query(String tableOrView, String[] columns, String where, String[] whereArgs, String group, String having, String sort){
        Cursor result = getReadableDatabase().query(tableOrView, columns, where, whereArgs, group, having, sort);
        return result;
    }

    public synchronized int deleteRows(String table, String where, String... whereArgs) {
        int rows = getWritableDatabase().delete(table, where, whereArgs);
        return rows;
    }

    public synchronized Cursor rawQuery(String query, String... selectArgs){
        return getWritableDatabase().rawQuery(query, selectArgs);
    }

    public synchronized void execSQL(String sql){
        getWritableDatabase().execSQL(sql);
    }
}
