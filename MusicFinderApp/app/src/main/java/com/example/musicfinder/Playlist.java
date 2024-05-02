package com.example.musicfinder;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class Playlist {
    private String title = "";
    private List<Song> songs = null;
    private List<String> filters = new ArrayList<>();
    private long playlistID = -1;

    public Playlist() {

    }
    public List<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<String> getFilters() {
        return this.filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public long getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(long playlistID) {
        this.playlistID = playlistID;
    }
    public void addSongToPlaylist(Song song) {
        this.songs.add(song);
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder output = new StringBuilder("Playlist (\n");
        for (Song song: this.songs) {
            output.append(song.toString()).append("\n");
        }
        output.append(")\nFilters (\n");
        for (String filter : this.filters) {
            output.append(filter).append("\n");
        }
        output.append(")");
        return output.toString();
    }

}
