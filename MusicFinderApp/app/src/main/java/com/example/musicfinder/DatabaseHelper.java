package com.example.musicfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String FAVOURITES_TABLE = "FAVOURITES_TABLE";
    public static final String HISTORY_TABLE = "HISTORY_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_PLAYLIST = "PLAYLIST";

    //ex: DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
    public DatabaseHelper(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFavouritesTableStatement = "CREATE TABLE " + FAVOURITES_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PLAYLIST + " TEXT)";
        String createHistoryTableStatement = "CREATE TABLE " + HISTORY_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PLAYLIST + " TEXT)";

        db.execSQL(createFavouritesTableStatement);
        db.execSQL(createHistoryTableStatement);
    }

    //called when database version number changes to prevent users apps from breaking when database
    //design changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean addPlaylistToTable(String tableName, Playlist playlist)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PLAYLIST, playlist.toString());

        long insert = db.insert(tableName, null, cv);
        return insert != -1;
    };

    public boolean deletePlaylistFromTable(String tableName, Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + tableName + " WHERE " + COLUMN_PLAYLIST + " = " + playlist.toString(); //this is not very efficient cz playlist string is huge, but it should work lol

        Cursor cursor = db.rawQuery(queryString, null);

        return cursor.moveToFirst();

    }

    // returns all the playlists as a list of string so we can more easily pass it to gpt
    public List<String> getAllFromTable(String tableName) {
        List<String> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String playlist = cursor.getString(1);
                returnList.add(playlist);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }
}