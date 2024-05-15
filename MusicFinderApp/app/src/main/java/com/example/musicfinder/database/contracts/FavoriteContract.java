package com.example.musicfinder.database.contracts;

import android.provider.BaseColumns;

public class FavoriteContract {
    private FavoriteContract() {}

    public static class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_PLAYLIST_ID = "playlist_id";
        // Add other columns as needed
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
            FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoriteEntry.COLUMN_PLAYLIST_ID + " INTEGER," +
            "FOREIGN KEY(" + FavoriteEntry.COLUMN_PLAYLIST_ID + ") REFERENCES " +
            PlaylistContract.PlaylistEntry.TABLE_NAME + "(" + PlaylistContract.PlaylistEntry._ID + "))";
}
