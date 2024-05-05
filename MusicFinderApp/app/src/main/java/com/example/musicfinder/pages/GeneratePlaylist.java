package com.example.musicfinder.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicfinder.Playlist;
import com.example.musicfinder.R;
import com.example.musicfinder.Song;
import com.example.musicfinder.SongAdapter;
import com.example.musicfinder.utils.ActivityUtil;
import com.example.musicfinder.utils.BackendHelper;

import java.util.ArrayList;
import java.util.List;

public class GeneratePlaylist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;

    private ProgressBar loadingAnim;
    private TextView noInternetTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_playlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActivityUtil.setNavigationDrawerEvents(this);

        View backImage = findViewById(R.id.imageViewBack);
        backImage.setOnClickListener(v -> openHomePage());

        recyclerView = findViewById(R.id.playlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadingAnim = findViewById(R.id.progressBarGenPlaylist);
        noInternetTextView = findViewById(R.id.textViewNoInternet);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                openHomePage();
            }
        });

        generatePlaylist();
    }

    private void generatePlaylist() {
        loadingAnim.setVisibility(View.VISIBLE);
        noInternetTextView.setVisibility(View.INVISIBLE);
        new Thread(() -> {
            int numberOfSongs = ActivityUtil.getAmountOfSongs();
            List<String> filters = ActivityUtil.getFilters();
            Playlist playlist = BackendHelper.requestPlaylist(numberOfSongs, filters);
            List<Song> songsArray;
            if (playlist != null) {
                 songsArray = playlist.getSongs();
            } else {
                songsArray = null;
            }

            // TODO: Add to database history


            runOnUiThread(()-> {
                if (songsArray != null) {
                    adapter.setSongs(songsArray);
                    recyclerView.setAdapter(adapter);
                    loadingAnim.setVisibility(View.INVISIBLE);
                }
                else {
                    loadingAnim.setVisibility(View.INVISIBLE);
                    noInternetTextView.setVisibility(View.VISIBLE);
                }
            });

        }).start();
    }

    private void openHomePage() {
        if (ActivityUtil.isPageNotLoading(loadingAnim)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}