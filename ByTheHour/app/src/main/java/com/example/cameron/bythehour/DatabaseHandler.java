package com.example.cameron.bythehour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cameron on 9/17/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "by_the_hour.db",
            TABLE_TIMES = "timest",
            KEY_ID = "id",
            KEY_DAY = "day",
            KEY_START = "start",
            KEY_END = "end",
            KEY_HOURS = "hours";

    public DatabaseHandler(Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TIMES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DAY + " TEXT," + KEY_START + " TEXT," + KEY_END + " TEXT," + KEY_HOURS + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMES);

        onCreate(db);
    }

    public void createTimestamp(Timestamp timestamp) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DAY, timestamp.get_day());
        values.put(KEY_START, timestamp.get_start());
        values.put(KEY_END, timestamp.get_end());
        values.put(KEY_HOURS, timestamp.get_hours());

        db.insert(TABLE_TIMES, null, values);
        db.close();
    }

    public Timestamp getTimestamp(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TIMES, new String[] {KEY_ID,KEY_DAY,KEY_START,KEY_END,KEY_HOURS}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor!=null)
            cursor.moveToFirst();

        Timestamp timestamp = new Timestamp(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        db.close();
        cursor.close();
        return timestamp;
    }

    public void deleteTimestamp(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TIMES, KEY_ID + "=?", new String[] {id});
        db.close();
    }

    public int getTimestampsCount() {
        //SELECT * FROM CONTACTS
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TIMES, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateTimestamp(Timestamp timestamp) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DAY, timestamp.get_day());
        values.put(KEY_START, timestamp.get_start());
        values.put(KEY_END, timestamp.get_end());
        values.put(KEY_HOURS, timestamp.get_hours());

        return db.update(TABLE_TIMES, values, KEY_ID + "=?", new String[] { String.valueOf(timestamp.get_id()) } );
    }

    public List<Timestamp> getAllTimestamps() {
        List<Timestamp> timestamps = new ArrayList<Timestamp>();

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TIMES, null);

        if (cursor.moveToFirst()) {
            do {
                Timestamp timestamp = new Timestamp(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                timestamps.add(timestamp);
            }
            while (cursor.moveToNext());
        }
        return timestamps;
    }

}