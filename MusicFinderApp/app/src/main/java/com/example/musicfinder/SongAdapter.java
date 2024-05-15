package com.example.musicfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private List<Song> songs;

    public SongAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.songNumTextView.setText(String.valueOf(position + 1));
        holder.songTitleTextView.setText(song.getTitle());
        holder.songArtistTextView.setText(song.getArtist());
        holder.playButtonImageView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<Song> newSongs) {
        songs = newSongs;
    }


    public void clearSongs() {songs.clear();}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songNumTextView, songTitleTextView, songArtistTextView;
        ImageView playButtonImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songNumTextView = itemView.findViewById(R.id.songNumberTextView);
            songTitleTextView = itemView.findViewById(R.id.songTitleTextView);
            songArtistTextView = itemView.findViewById(R.id.songArtistTextView);
            playButtonImageView = itemView.findViewById(R.id.playImage);
        }
    }
}
