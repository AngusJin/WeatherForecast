package com.sitan.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;
    private static final String TABLE_NAME = "city_list";

    public boolean isDBNull() {
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME) == 0;
    }

    public long getDBRows() {
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }


    public DBManager(Context context) {
        helper = new DBHelper(context);
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

    public void parseListIntoDB(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                insert(obj.getInt("id"), obj.getInt("pid"), obj.getString("city_code"), obj.getString("city_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(int id, int pid, String city_code, String city_name) {
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", id);
            cv.put("pid", pid);
            cv.put("city_code", city_code);
            cv.put("city_name", city_name);
            db.insert(TABLE_NAME, "id", cv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    // pid=0是parent
    public List<String> getParentCityList() {
        if (!isDBNull()) {
            List<String> list = new ArrayList<>();
            Cursor c = db.rawQuery("select id, city_name, city_code from " + TABLE_NAME + " where pid=0", null);
            for (c.moveToNext(); !c.isAfterLast(); c.moveToNext()) {
                String str = c.getString(1) + "\t\t\t\t" + c.getString(2);//北京 101010100
                list.add(str);
            }
            return list;
        } else
            return null;
    }

    //pid=id
    public List<String> getCityList(int id) {
        if (!isDBNull()) {
            List<String> list = new ArrayList<>();
            Cursor c = db.rawQuery("select city_name, city_code from " + TABLE_NAME + " where pid=" + id, null);
            for (c.moveToNext(); !c.isAfterLast(); c.moveToNext()) {
                String str = c.getString(0) + "\t\t\t\t" + c.getString(1);
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
