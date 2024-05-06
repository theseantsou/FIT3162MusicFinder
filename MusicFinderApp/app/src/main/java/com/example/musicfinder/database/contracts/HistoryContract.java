package com.example.musicfinder.database.contracts;

import android.provider.BaseColumns;

public class HistoryContract {
    private HistoryContract() {}

    public static class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_PLAYLIST_ID = "playlist_id";
        public static final String COLUMN_TIME_ADDED = "time_added";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
            HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            HistoryEntry.COLUMN_PLAYLIST_ID + " INTEGER," +
            HistoryEntry.COLUMN_TIME_ADDED + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(" + HistoryEntry.COLUMN_PLAYLIST_ID + ") REFERENCES " +
            PlaylistContract.PlaylistEntry.TABLE_NAME + "(" + PlaylistContract.PlaylistEntry._ID + "))";
}
