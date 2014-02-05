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

    /**
     * Query call for a dynamic number of arguments. It's a convenience call for HipQueryTask.<br/>
     * Can take any number of arguments, in this order:<br/>
     * table/view<br/>
     * sort, optional - can be null<br/>
     * where string<br/>
     * where argument 1<br/>
     * where argument 2<br/>
     * where argument N...<br/>
     * <br/>
     * Any arguments after the first (table) are optional and can be skipped.<br/>
     * This call does not support group/having/column arguments, use a DB view or the other query calls.
     * @param args
     * @return
     */
    public Cursor query(String... args){
        switch (args.length){
            case 0:
                throw new IllegalArgumentException("You must include at least one argument (table). See HipDatabase.query(string[])");
            case 1:
                return query(args[0], null, null, null, null, null, null);
            case 2:
                return query(args[0], null, null, null, null, null, args[1]);
            case 3:
                return query(args[0], null, args[2], null, null, null, args[1]);
        }
        String[] whereArgs = new String[args.length-3];
        //we could use Arrays.copyOf here, but it's only available from API9, this app targets API8
        for(int ix=3; ix<args.length; ix++){
            whereArgs[ix-3] = args[ix];
        }
        return query(args[0], null, args[2], whereArgs, null, null, args[1]);
    }

    public synchronized Cursor query(String tableView, String[] columns, String where, String[] whereArgs, String group, String having, String sort){
        Cursor result = getReadableDatabase().query(tableView, columns, where, whereArgs, group, having, sort);
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
