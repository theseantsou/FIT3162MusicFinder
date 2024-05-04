package com.example.musicfinder;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements LimitButtonClickOnce{

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
        button.setOnClickListener(v -> openFilterPage());
    }

    /**
     * Function that opens the filter page
     * Make sure that the button can only be clicked once to prevent
     * multiple instance of the filter page opening
     */
    public void openFilterPage() {
        if (isButtonClickable) {
            isButtonClickable = false; // Disable button

//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BackendHelper.baseURL + "/login"));
//            launcher.launch(intent);
            // Launch the SelectFilter page
            Intent intent = new Intent(this, SelectMood.class);
            launcher.launch(intent);
        }
    }

}