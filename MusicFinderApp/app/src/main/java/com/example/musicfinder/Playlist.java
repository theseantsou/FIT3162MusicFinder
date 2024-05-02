package com.example.musicfinder;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class Playlist {
    private List<Song> songs = null;
    private List<String> filters = new ArrayList<>();
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

    public void addSongToPlaylist(Song song) {
        this.songs.add(song);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Song song: songs) {
            output.append(song.toString()).append("\n");
        }
        return output.toString();
    }
}
