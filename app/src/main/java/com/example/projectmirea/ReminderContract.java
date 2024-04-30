package com.example.projectmirea;

import android.provider.BaseColumns;

public class ReminderContract {
    private ReminderContract(){
    }
    public static class ReminderEntry implements BaseColumns{
        public static final String TABLE_NAME = "reminders";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
    }
}
