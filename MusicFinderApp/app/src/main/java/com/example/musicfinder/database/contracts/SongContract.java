package com.example.musicfinder.database.contracts;

import android.provider.BaseColumns;

public class SongContract {
    private SongContract() {}

    public static class SongEntry implements BaseColumns {
        public static final String TABLE_NAME = "song";
        public static final String COLUMN_PLAYLIST_ID = "playlist_id";
        public static final String COLUMN_ARTIST = "artist";
        public static final String COLUMN_TITLE = "title";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + SongEntry.TABLE_NAME + " (" +
            SongEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            SongEntry.COLUMN_PLAYLIST_ID + " INTEGER," +
            SongEntry.COLUMN_ARTIST + " TEXT," +
            SongEntry.COLUMN_TITLE + " TEXT," +
            "FOREIGN KEY(" + SongEntry.COLUMN_PLAYLIST_ID + ") REFERENCES " +
            PlaylistContract.PlaylistEntry.TABLE_NAME + "(" + PlaylistContract.PlaylistEntry._ID + "))";
}
