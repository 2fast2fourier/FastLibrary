package com.salvadordalvik.fastlibrary.data;

import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by matthewshepard on 3/27/14.
 */
public class SimpleArrayQuery {
    private enum TYPE {INT, LONG, DOUBLE, FLOAT, STRING}

    public static void queryIntAsync(FastDatabase db, IntResultCallback callback, String table, String column, String sort, String where, String... whereArgs){
        new ArrayQueryTask(callback, db, table, column, sort, where, whereArgs).execute();
    }

    public static void queryLongAsync(FastDatabase db, LongResultCallback callback, String table, String column, String sort, String where, String... whereArgs){
        new ArrayQueryTask(callback, db, table, column, sort, where, whereArgs).execute();
    }

    public static void queryDoubleAsync(FastDatabase db, DoubleResultCallback callback, String table, String column, String sort, String where, String... whereArgs){
        new ArrayQueryTask(callback, db, table, column, sort, where, whereArgs).execute();
    }

    public static void queryFloatAsync(FastDatabase db, FloatResultCallback callback, String table, String column, String sort, String where, String... whereArgs){
        new ArrayQueryTask(callback, db, table, column, sort, where, whereArgs).execute();
    }

    public static void queryStringAsync(FastDatabase db, StringResultCallback callback, String table, String column, String sort, String where, String... whereArgs){
        new ArrayQueryTask(callback, db, table, column, sort, where, whereArgs).execute();
    }

    public static int[] queryIntImmediate(FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
        return processIntArray(db.query(table, sort, where, whereArgs), column);
    }

    public static long[] queryLongImmediate(FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
        return processLongArray(db.query(table, sort, where, whereArgs), column);
    }

    public static double[] queryDoubleImmediate(FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
        return processDoubleArray(db.query(table, sort, where, whereArgs), column);
    }

    public static float[] queryFloatImmediate(FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
        return processFloatArray(db.query(table, sort, where, whereArgs), column);
    }

    public static String[] queryStringImmediate(FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
        return processStringArray(db.query(table, sort, where, whereArgs), column);
    }

    private static class ArrayQueryTask extends AsyncTask<Void, Void, QueryResult> {
        private final FastDatabase db;
        private final String table;
        private final String column;
        private final String sort;
        private final String where;
        private final String[] whereArgs;
        private final TYPE type;

        private IntResultCallback intCallback;
        private LongResultCallback longCallback;
        private DoubleResultCallback doubleCallback;
        private FloatResultCallback floatCallback;
        private StringResultCallback stringCallback;

        protected ArrayQueryTask(IntResultCallback callback, FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
            this(TYPE.INT, db, table, column, sort, where, whereArgs);
            intCallback = callback;
        }

        protected ArrayQueryTask(LongResultCallback callback, FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
            this(TYPE.LONG, db, table, column, sort, where, whereArgs);
            longCallback = callback;
        }

        protected ArrayQueryTask(DoubleResultCallback callback, FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
            this(TYPE.DOUBLE, db, table, column, sort, where, whereArgs);
            doubleCallback = callback;
        }

        protected ArrayQueryTask(FloatResultCallback callback, FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
            this(TYPE.FLOAT, db, table, column, sort, where, whereArgs);
            floatCallback = callback;
        }

        protected ArrayQueryTask(StringResultCallback callback, FastDatabase db, String table, String column, String sort, String where, String... whereArgs){
            this(TYPE.STRING, db, table, column, sort, where, whereArgs);
            stringCallback = callback;
        }

        private ArrayQueryTask(TYPE type, FastDatabase db, String table, String column, String sort, String where, String... whereArgs) {
            this.type = type;
            this.db = db;
            this.table = table;
            this.column = column;
            this.sort = sort;
            this.where = where;
            this.whereArgs = whereArgs;
        }

        @Override
        protected QueryResult doInBackground(Void... params) {
            return new QueryResult(db.query(table, sort, where, whereArgs), column, type);
        }

        @Override
        protected void onPostExecute(QueryResult queryResult) {
            super.onPostExecute(queryResult);
            switch (type){
                case INT:
                    intCallback.onQueryResult(queryResult.ints);
                    break;
                case LONG:
                    longCallback.onQueryResult(queryResult.longs);
                    break;
                case DOUBLE:
                    doubleCallback.onQueryResult(queryResult.doubles);
                    break;
                case FLOAT:
                    floatCallback.onQueryResult(queryResult.floats);
                    break;
                case STRING:
                    stringCallback.onQueryResult(queryResult.strings);
                    break;
            }
        }
    }

    public interface IntResultCallback{
        public void onQueryResult(int[] values);
    }

    public interface LongResultCallback{
        public void onQueryResult(long[] values);
    }

    public interface DoubleResultCallback{
        public void onQueryResult(double[] values);
    }

    public interface FloatResultCallback{
        public void onQueryResult(float[] values);
    }

    public interface StringResultCallback{
        public void onQueryResult(String[] values);
    }

    private static class QueryResult {
        private QueryResult(Cursor data, String column, TYPE type){
            switch (type){
                case INT:
                    ints = processIntArray(data, column);
                    break;
                case LONG:
                    longs = processLongArray(data, column);
                    break;
                case DOUBLE:
                    doubles = processDoubleArray(data, column);
                    break;
                case FLOAT:
                    floats = processFloatArray(data, column);
                    break;
                case STRING:
                    strings = processStringArray(data, column);
                    break;
            }
        }
        int[] ints;
        long[] longs;
        double[] doubles;
        float[] floats;
        String[] strings;
    }

    private static int[] processIntArray(Cursor data, String column){
        if(data != null && data.moveToFirst()){
            int columnId = data.getColumnIndex(column);
            if(columnId < 0){
                throw new IllegalArgumentException("Invalid column: "+column);
            }
            int count = data.getCount();
            int[] ints = new int[count];
            for(int ix=0; ix<count; ix++){
                ints[ix] = data.getInt(columnId);
                data.moveToNext();
            }
            return ints;
        }else{
            return new int[0];
        }
    }

    private static long[] processLongArray(Cursor data, String column){
        if(data != null && data.moveToFirst()){
            int columnId = data.getColumnIndex(column);
            if(columnId < 0){
                throw new IllegalArgumentException("Invalid column: "+column);
            }
            int count = data.getCount();
            long[] longs = new long[count];
            for(int ix=0; ix<count; ix++){
                longs[ix] = data.getLong(columnId);
                data.moveToNext();
            }
            return longs;
        }else{
            return new long[0];
        }
    }

    private static float[] processFloatArray(Cursor data, String column){
        if(data != null && data.moveToFirst()){
            int columnId = data.getColumnIndex(column);
            if(columnId < 0){
                throw new IllegalArgumentException("Invalid column: "+column);
            }
            int count = data.getCount();
            float[] floats = new float[count];
            for(int ix=0; ix<count; ix++){
                floats[ix] = data.getFloat(columnId);
                data.moveToNext();
            }
            return floats;
        }else{
            return new float[0];
        }
    }

    private static double[] processDoubleArray(Cursor data, String column){
        if(data != null && data.moveToFirst()){
            int columnId = data.getColumnIndex(column);
            if(columnId < 0){
                throw new IllegalArgumentException("Invalid column: "+column);
            }
            int count = data.getCount();
            double[] doubles = new double[count];
            for(int ix=0; ix<count; ix++){
                doubles[ix] = data.getDouble(columnId);
                data.moveToNext();
            }
            return doubles;
        }else{
            return new double[0];
        }
    }

    private static String[] processStringArray(Cursor data, String column){
        if(data != null && data.moveToFirst()){
            int columnId = data.getColumnIndex(column);
            if(columnId < 0){
                throw new IllegalArgumentException("Invalid column: "+column);
            }
            int count = data.getCount();
            String[] strings = new String[count];
            for(int ix=0; ix<count; ix++){
                strings[ix] = data.getString(columnId);
                data.moveToNext();
            }
            return strings;
        }else{
            return new String[0];
        }
    }
}
