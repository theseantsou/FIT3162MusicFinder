package com.example.musicfinder;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicfinder.pages.GeneratePlaylist;
import com.example.musicfinder.utils.ActivityUtil;
import com.example.musicfinder.utils.BackendHelper;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private List<Song> songs;

    private Activity parentActivity;

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Song songPlaying;
    private ImageView previewSongPlayed;

    public SongAdapter(List<Song> songs) {
        this.songs = songs;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
        });

        // Handle errors during preparation
        mediaPlayer.setOnErrorListener((mp, what, extra) -> false);
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
            if (!isPlaying) {

                isPlaying = true;
                holder.playButtonImageView.setImageResource(R.drawable.pause_icon);
                String email = ActivityUtil.getEmailFromSharedPref(parentActivity);
                new Thread(() -> {
                    String preview_url = BackendHelper.getSongsPreview(email, song);
                    if (preview_url != null && !preview_url.equals("null")) {
                        try {
                            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build());
                            mediaPlayer.setDataSource(preview_url);
                            mediaPlayer.prepare(); // Prepare asynchronously to avoid blocking the UI thread
                        } catch (Exception e) {
                            parentActivity.runOnUiThread(() -> Toast.makeText(parentActivity, "Failed to load audio", Toast.LENGTH_SHORT).show());

                        }
                    }

                }).start();
            }
            else {
                isPlaying = false;
                holder.playButtonImageView.setImageResource(R.drawable.play_icon);
                mediaPlayer.pause();
                mediaPlayer.release();
            }


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
