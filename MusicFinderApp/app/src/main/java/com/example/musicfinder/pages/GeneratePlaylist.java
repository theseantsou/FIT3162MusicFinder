package com.example.musicfinder.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicfinder.LimitButtonClickOnce;
import com.example.musicfinder.Playlist;
import com.example.musicfinder.R;
import com.example.musicfinder.Song;
import com.example.musicfinder.SongAdapter;
import com.example.musicfinder.UseSpotify;
import com.example.musicfinder.utils.ActivityUtil;
import com.example.musicfinder.utils.BackendHelper;

import java.util.ArrayList;
import java.util.List;

public class GeneratePlaylist extends AppCompatActivity implements LimitButtonClickOnce, UseSpotify {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private boolean isButtonClickable = true;
    private ProgressBar loadingAnim;
    private TextView noInternetTextView;
    private ActivityResultLauncher<Intent> launcher;
    private Playlist generatedPlaylist;
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

        launcher = ActivityUtil.getResultLauncher(this);

        generatePlaylist(ActivityUtil.getAmountOfSongs(), ActivityUtil.getFilters());

        AppCompatButton saveToSpotifyButton = findViewById(R.id.saveSpotifyButton);
        saveToSpotifyButton.setOnClickListener(v -> addPlaylistToSpotify());

        AppCompatButton regenButton = findViewById(R.id.regenerateButton);
        regenButton.setOnClickListener(v -> {
            adapter.setSongs(new ArrayList<>());
            generatePlaylist(generatedPlaylist.getSongs().size(), generatedPlaylist.getFilters());
        });
    }

    private void generatePlaylist(int numberOfSongs, List<String> filters) {
        loadingAnim.setVisibility(View.VISIBLE);
        noInternetTextView.setVisibility(View.INVISIBLE);
        new Thread(() -> {
            generatedPlaylist = BackendHelper.requestPlaylist(numberOfSongs, filters);
            List<Song> songsArray;
            if (generatedPlaylist != null) {
                 songsArray = generatedPlaylist.getSongs();
            } else {
                songsArray = null;
            }


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

            // TODO: Add to history table

        }).start();
    }

    private void openHomePage() {
        if (ActivityUtil.isPageNotLoading(loadingAnim)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    private void addPlaylistToSpotify() {

        if (isButtonClickable && ActivityUtil.isPageNotLoading(loadingAnim) && ActivityUtil.hasNoInternetWarning(noInternetTextView)) {
            isButtonClickable = false;
            String email = ActivityUtil.getEmailFromSharedPref(this);
            new Thread(() -> {
                String url = BackendHelper.getSpotifyLoginURL(email);

                if (url != null && !url.equals("null")) {
                    Intent intent = new Intent(this, SpotifyWebActivity.class);
                    intent.putExtra("url", url);
                    launcher.launch(intent);
                }
                else {
                    onLeaveSpotifyPage();
                }
            }).start();
        }

    }

    @Override
    public void onLeaveSpotifyPage() {
        isButtonClickable = false;
        String email = ActivityUtil.getEmailFromSharedPref(this);
        List<Song> songsList = generatedPlaylist.getSongs();
        String title = generatedPlaylist.getTitle();
        new Thread(() -> {
            boolean isSuccessful = BackendHelper.savePlaylistToSpotify(email, songsList, title);
            if (isSuccessful) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Playlist added to Spotify", Toast.LENGTH_SHORT).show();
                });
            }
            else {
                Toast.makeText(this, "Failed to add playlist to Spotify", Toast.LENGTH_SHORT).show();
            }
            
            isButtonClickable = true;

        }).start();


    }

    @Override
    public void setButtonClickable(boolean buttonClickable) {
        this.isButtonClickable = buttonClickable;
    }
}