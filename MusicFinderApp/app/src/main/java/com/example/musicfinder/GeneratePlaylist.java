package com.example.musicfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeneratePlaylist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
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
        adapter = new SongAdapter(new ArrayList<>()); // Empty list initially
        recyclerView.setAdapter(adapter);

        generatePlaylist();
    }

    private void generatePlaylist() {
        new Thread(() -> {
            int numberOfSongs = ActivityUtil.getAmountOfSongs();
            List<String> filters = ActivityUtil.getFilters();
            List<Song> songsArray = BackendHelper.requestPlaylist(numberOfSongs, filters);

            runOnUiThread(()-> {
                if (songsArray != null) {
                    adapter.setSongs(songsArray);
                    recyclerView.setAdapter(adapter);

                }
            });

        }).start();
    }

    private void openHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}