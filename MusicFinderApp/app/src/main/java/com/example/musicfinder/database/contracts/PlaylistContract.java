package com.example.musicfinder.database.contracts;

import android.provider.BaseColumns;

public class PlaylistContract {

    private PlaylistContract() {}

    public static class PlaylistEntry implements BaseColumns {
        public static final String TABLE_NAME = "playlist";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_FILTER = "filter";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + PlaylistEntry.TABLE_NAME + " (" +
                    PlaylistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PlaylistEntry.COLUMN_TITLE + " TEXT," +
                    PlaylistEntry.COLUMN_FILTER + " TEXT)";
}
