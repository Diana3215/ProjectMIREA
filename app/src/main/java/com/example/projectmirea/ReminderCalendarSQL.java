package com.example.projectmirea;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReminderCalendarSQL extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;

    public ReminderCalendarSQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы для хранения напоминаний
        final String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE reminders (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "date TEXT);";

        db.execSQL(SQL_CREATE_REMINDER_TABLE);

        Context ReminderCalendarSQL = null;
        ReminderCalendarSQL dbHelper = new ReminderCalendarSQL(ReminderCalendarSQL);
        Cursor cursor = dbHelper.getAllReminders();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));


                Log.d("Reminder", "ID: " + id + ", Title: " + title + ", Date: " + date);
            }


            cursor.close();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reminders");
        onCreate(db);
    }

    public long addReminder(String title, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("date", date);
        long result = db.insert("reminders", null, values);
        db.close();
        return result;
    }

    public Cursor getAllReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM reminders", null);
    }
    public int deleteReminderByTitle(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "title = ?";
        String[] whereArgs = {title};
        int deletedRows = db.delete("reminders", whereClause, whereArgs);
        db.close();
        return deletedRows;
    }


}
