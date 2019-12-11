package com.sitan.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class FavoDBManager {
    private FavoDBHelper helper;
    private SQLiteDatabase db;
    private static final String TABLE_NAME = "favorites";

    public boolean isDBNull() {
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME) == 0;
    }

    public long getDBRows() {
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }


    public FavoDBManager(Context context) {
        helper = new FavoDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void clear() {
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, "id>0", null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public void insert(int id, String city_code, String parent_city_name, String city_name) {
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", id);
            cv.put("city_code", city_code);
            cv.put("parent_city_name", parent_city_name);
            cv.put("city_name", city_name);
            db.insert(TABLE_NAME, "id", cv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void delete(int id) {
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
            db.execSQL("update " + TABLE_NAME + " set id=id-1 where id>" + id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void delete(String city_code) {
        db.beginTransaction();
        try {
            Cursor c = db.rawQuery("select id from " + TABLE_NAME + " where city_code=" + city_code, null);
            c.moveToFirst();
            int id = c.getInt(0);
            db.delete(TABLE_NAME, "city_code=?", new String[]{String.valueOf(city_code)});
            db.execSQL("update " + TABLE_NAME + " set id=id-1 where id>" + id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

//    public int queryId(String city_code) {
//        Cursor c = db.rawQuery("select id, city_code from " + TABLE_NAME + " where city_code=" + city_code, null);
//        c.moveToFirst();
//        //System.out.println("**********************" + c.getString(1));
//        return 1;
//    }

    public int getLastId() {
        Cursor c = db.rawQuery("select id from " + TABLE_NAME, null);
        c.moveToLast();
        return c.getInt(0);
    }

    public int getNewId() {
        Cursor c = db.rawQuery("select id from " + TABLE_NAME, null);
        if (c.moveToLast()) {
            return c.getInt(0) + 1;
        } else
            return 1;
    }

    public boolean isExist(String city_code) {
        Cursor c = db.rawQuery("select city_code from " + TABLE_NAME, null);
        for (c.moveToNext(); !c.isAfterLast(); c.moveToNext()) {
            if (c.getString(0).equals(city_code))
                return true;
        }
        return false;
    }

    public String getCityCode(int id) {
        Cursor c = db.rawQuery("select city_code from " + TABLE_NAME + " where id=?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        return c.getString(0);
    }

    public List<String> getListTitle() {
        if (!isDBNull()) {
            List<String> list = new ArrayList<>();
            Cursor c = db.rawQuery("select id, parent_city_name, city_name from " + TABLE_NAME, null);
            for (c.moveToNext(); !c.isAfterLast(); c.moveToNext()) {
                String str = "";
                if (c.getString(1).equals("") && !c.getString(2).equals(""))
                    str += c.getString(2);
                else
                    str += c.getString(1) + "\t\t\t" + c.getString(2);
                list.add(str);
            }
            return list;
        } else
            return null;
    }

    public void closeDB() {
        db.close();
    }
}
