package com.example.musicfinder.pages;


import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResultLauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicfinder.LimitButtonClickOnce;
import com.example.musicfinder.R;
import com.example.musicfinder.utils.ActivityUtil;
import com.example.musicfinder.utils.BackendHelper;

public class MainActivity extends AppCompatActivity implements LimitButtonClickOnce {

    @Override
    public void setButtonClickable(boolean buttonClickable) {
        isButtonClickable = buttonClickable;
    }

    private boolean isButtonClickable;
    private ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.isButtonClickable = true;

        // Set the navigation menu events
        ActivityUtil.setNavigationDrawerEvents(this);
        // Get the next page launcher
        launcher = ActivityUtil.getResultLauncher(this);

        // Set the on click listener to open the filter page
        AppCompatButton button = findViewById(R.id.button);
        button.setOnClickListener(v -> openNextPage());
    }

    /**
     * Function that opens the filter page
     * Make sure that the button can only be clicked once to prevent
     * multiple instance of the filter page opening
     */
    public void openNextPage() {
        if (isButtonClickable) {
            isButtonClickable = false; // Disable button


            Thread thread = new Thread(() -> {
                String email = ActivityUtil.getEmailFromSharedPref(this);
                String url = BackendHelper.getSpotifyLoginURL(email);
                if (url != null && !url.equals("null")) {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, SpotifyWebActivity.class);
                        intent.putExtra("url", url);
                        launcher.launch(intent);
                    });
                }
                else {
                    Intent intent = new Intent(this, SelectMood.class);
                    launcher.launch(intent);
                }
            });

            thread.start();

        }
    }

}