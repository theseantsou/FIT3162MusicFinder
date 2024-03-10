package com.example.musicfinder;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView navigationIcon;

    private boolean isButtonClickable = true;

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

        ActivityUtil.setNavigationDrawerEvents(this);


    }


    // TODO: FIX THIS ENSURE ONLY ONE INSTANCE IS OPENED
    public void openArtistPage(View view) {
        if (isButtonClickable) {
            isButtonClickable = false; // Disable button

            Intent intent = new Intent(this, SelectFilter.class);
            startActivity(intent);

        }
    }
    // TODO: FIX THIS ENSURE ONLY ONE INSTANCE IS OPENED
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(requestCode);
        builder.show();
        if (requestCode == ActivityUtil.REQUEST_CODE_SELECT_ARTIST) {
            // Re-enable button after activity result

            isButtonClickable = true;
        }
    }

}