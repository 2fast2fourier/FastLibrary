package com.salvadordalvik.fastlibrary.request;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.salvadordalvik.fastlibrary.data.FastDatabase;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by matthewshepard on 3/17/14.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PersistentCookieStore implements CookieStore {
    private final CookieDatabase cookieDB;

    public PersistentCookieStore(Context context){
        cookieDB = new CookieDatabase(context.getApplicationContext(), "fastcookies.db");
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        cookieDB.deleteRows("cookies", "cookie_expires<?", getExpireDate());
        if(cookie.getMaxAge() != 0){
            ContentValues cv = new ContentValues();
            cv.put("cookie_uid", getCookieUID(uri, cookie));
            cv.put("cookie_uri", uri != null ? uri.getHost() : null);
            cv.put("cookie_domain", cookie.getDomain());
            cv.put("cookie_path", cookie.getPath());
            cv.put("cookie_name", cookie.getName());
            cv.put("cookie_value", cookie.getValue());
            cv.put("cookie_max_age", cookie.getMaxAge());
            cv.put("cookie_comment", cookie.getComment());
            cv.put("cookie_comment_url", cookie.getCommentURL());
            cv.put("cookie_port_list", cookie.getPortlist());
            cv.put("cookie_version", cookie.getVersion());
            cv.put("cookie_discard", cookie.getDiscard());
            cv.put("cookie_secure", cookie.getSecure());
            cv.put("cookie_expires", getExpireDate(cookie.getMaxAge()));
            cookieDB.insertRows("cookies", SQLiteDatabase.CONFLICT_REPLACE, cv);
        }else{
            Log.w("PersistentCookieStore", "Ignoring cookie with invalid expiration: "+cookie.toString());
        }
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
        Cursor data = cookieDB.query("cookies", null, "cookie_uri=? AND cookie_expires>=?", uri.getHost(), getExpireDate());
        int[] columns = CookieDatabase.getColumnIndicies(data);
        try{
            if(data.moveToFirst()){
                do{
                    cookies.add(makeCookie(data, columns));
                }while(data.moveToNext());
            }
        }finally {
            data.close();
        }
        return Collections.unmodifiableList(cookies);
    }

    @Override
    public List<HttpCookie> getCookies() {
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
        Cursor data = cookieDB.query("cookies", null, "cookie_expires>=?", getExpireDate());
        int[] columns = CookieDatabase.getColumnIndicies(data);
        try{
            if(data.moveToFirst()){
                do{
                    cookies.add(makeCookie(data, columns));
                }while(data.moveToNext());
            }
        }finally {
            data.close();
        }
        return Collections.unmodifiableList(cookies);
    }

    @Override
    public List<URI> getURIs() {
        ArrayList<URI> cookieUri = new ArrayList<URI>();
        Cursor data = cookieDB.query("cookies", null, "cookie_uri NOT NULL AND cookie_expires>=?", getExpireDate());
        int[] columns = CookieDatabase.getColumnIndicies(data);
        try{
            if(data.moveToFirst()){
                do{
                    cookieUri.add(new URI(data.getString(columns[1])));
                }while(data.moveToNext());
            }
        }catch(URISyntaxException e) {
            e.printStackTrace();
        }finally{
            data.close();
        }
        return Collections.unmodifiableList(cookieUri);
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return cookieDB.deleteRows("cookies", "cookie_uid=?", getCookieUID(uri, cookie)) > 0;
    }

    @Override
    public boolean removeAll() {
        return cookieDB.deleteRows("cookies", null) > 0;
    }

    private static String getCookieUID(URI uri, HttpCookie cookie){
        return (uri != null ? uri.getHost()+"-" : "")+cookie.getDomain()+"-"+cookie.getPath()+"-"+cookie.getName();
    }

    private static String getExpireDate(long maxAge){
        return Long.toString((System.currentTimeMillis()+(maxAge*1000)));
    }

    private static String getExpireDate(){
        return Long.toString(System.currentTimeMillis());
    }

    private static HttpCookie makeCookie(Cursor data, int[] columns){
        //see CookieDatabase.COLUMNS for column indexes
        HttpCookie cookie = new HttpCookie(data.getString(columns[4]), data.getString(5));
        cookie.setDomain(data.getString(columns[2]));
        cookie.setPath(data.getString(columns[3]));
        cookie.setMaxAge(data.getLong(columns[6]));
        cookie.setComment(data.getString(columns[7]));
        cookie.setCommentURL(data.getString(columns[8]));
        cookie.setPortlist(data.getString(columns[9]));
        cookie.setVersion(data.getInt(columns[10]));
        cookie.setDiscard(data.getInt(columns[11]) != 0);
        cookie.setSecure(data.getInt(columns[12]) != 0);
        return cookie;
    }

    private static class CookieDatabase extends FastDatabase {
        private CookieDatabase(Context context, String name) {
            super(context, name, null, 1);
        }

        public static final String[] COLUMNS = {
                "cookie_uid",
                "cookie_uri",
                "cookie_domain",
                "cookie_path",
                "cookie_name",
                "cookie_value",
                "cookie_max_age",
                "cookie_comment",
                "cookie_comment_url",
                "cookie_port_list",
                "cookie_version",
                "cookie_discard",
                "cookie_secure",
                "cookie_expires"
         };

        public static int[] getColumnIndicies(Cursor data){
            int[] columns = new int[CookieDatabase.COLUMNS.length];
            for(int ix=0; ix<CookieDatabase.COLUMNS.length; ix++){
                columns[ix] = data.getColumnIndex(CookieDatabase.COLUMNS[ix]);
            }
            return columns;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE cookies (" +
                    "cookie_uid TEXT UNIQUE NOT NULL," +
                    "cookie_uri TEXT," +
                    "cookie_domain TEXT NOT NULL," +
                    "cookie_path TEXT," +
                    "cookie_name TEXT NOT NULL," +
                    "cookie_value TEXT NOT NULL," +
                    "cookie_max_age INTEGER DEFAULT 0," +
                    "cookie_comment TEXT," +
                    "cookie_comment_url TEXT," +
                    "cookie_port_list TEXT," +
                    "cookie_version INTEGER NOT NULL," +
                    "cookie_discard INTEGER NOT NULL," +
                    "cookie_secure INTEGER NOT NULL," +
                    "cookie_expires INTEGER NOT NULL" +
                    ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
