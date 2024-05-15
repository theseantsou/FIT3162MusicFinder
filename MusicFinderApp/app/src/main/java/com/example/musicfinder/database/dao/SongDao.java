package com.example.musicfinder.database.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicfinder.Song;
import com.example.musicfinder.database.contracts.SongContract;

import java.util.List;

public class SongDao implements Dao<Song>{

    private SQLiteDatabase db;
    private long playlistId;
    public SongDao(SQLiteDatabase db, long playlistId) {
        this.db = db;
        this.playlistId = playlistId;
    }
    @Override
    public long insertItem(Song song) {
        ContentValues songValues = new ContentValues();
        songValues.put(SongContract.SongEntry.COLUMN_TITLE, song.getTitle());
        songValues.put(SongContract.SongEntry.COLUMN_ARTIST, song.getArtist());
        songValues.put(SongContract.SongEntry.COLUMN_PLAYLIST_ID, playlistId);
        return db.insert(SongContract.SongEntry.TABLE_NAME, null, songValues);
    }

    @Override
    public void deleteItem(Song item) {

    }

    @Override
    public List<Song> getAllItems() {
        return null;
    }

    @Override
    public Song getItemById(long id) {
        return null;
    }
}
