package com.example.musicfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;

import java.util.List;

public class GeneratePlaylist extends AppCompatActivity {

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
    }

    private void generatePlaylist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int numberOfSongs = ActivityUtil.getAmountOfSongs();
                List<String> filters = ActivityUtil.getFilters();
                JSONArray songsArray = BackendHelper.requestPlaylist(numberOfSongs, filters);
                System.out.println(songsArray);

            }
        }).start();
    }

    private void openHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}