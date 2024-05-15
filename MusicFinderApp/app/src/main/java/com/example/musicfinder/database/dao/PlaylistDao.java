package com.example.musicfinder.database.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicfinder.Playlist;
import com.example.musicfinder.Song;
import com.example.musicfinder.database.contracts.PlaylistContract;

import java.util.List;

public class PlaylistDao implements Dao<Playlist> {
    private SQLiteDatabase db;
    public PlaylistDao(SQLiteDatabase db) {
        this.db = db;
    }
    @Override
    public long insertItem(Playlist playlist) {
        ContentValues values = new ContentValues();
        values.put(PlaylistContract.PlaylistEntry.COLUMN_TITLE, playlist.getTitle());
        values.put(PlaylistContract.PlaylistEntry.COLUMN_FILTER, playlist.getFilters().toString());

        return db.insert("playlist", null, values);
    }

    @Override
    public void deleteItem(Playlist item) {

    }

    @Override
    public List<Playlist> getAllItems() {
        return null;
    }

    @Override
    public Playlist getItemById(long id) {
        return null;
    }
}
