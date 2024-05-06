package com.example.musicfinder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.musicfinder.Playlist;
import com.example.musicfinder.Song;
import com.example.musicfinder.database.contracts.FavoriteContract;
import com.example.musicfinder.database.contracts.HistoryContract;
import com.example.musicfinder.database.contracts.PlaylistContract;
import com.example.musicfinder.database.contracts.SongContract;
import com.example.musicfinder.database.dao.PlaylistDao;
import com.example.musicfinder.database.dao.SongDao;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "playlist.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlaylistContract.SQL_CREATE_TABLE);
        db.execSQL(SongContract.SQL_CREATE_TABLE);
        db.execSQL(FavoriteContract.SQL_CREATE_TABLE);
        db.execSQL(HistoryContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private long addPlaylistToTable(Playlist playlist, SQLiteDatabase db) {
        long playlistID = playlist.getPlaylistID();

        if (playlistID  == -1) {
            PlaylistDao playlistDao = new PlaylistDao(db);

            playlistID  = playlistDao.insertItem(playlist);
            List<Song> songList = playlist.getSongs();
            SongDao songDao = new SongDao(db, playlistID );
            songList.forEach(songDao::insertItem);
        }
        return playlistID;
    }
    public void addPlaylistToTable(String tableName, Playlist playlist)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long playlistID = addPlaylistToTable(playlist, db);

        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryEntry.COLUMN_PLAYLIST_ID, playlistID);
        db.insert(tableName, null, values);
    }

}
