package com.example.musicfinder;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView navigationIcon;
    private MenuItem favItem, historyItem;



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

        setNavigationDrawerEvents();
    }

    private void setNavigationDrawerEvents() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationIcon = findViewById(R.id.imageMenu);
        Menu menu = navigationView.getMenu();

        favItem = menu.findItem(R.id.nav_fav);
        historyItem = menu.findItem(R.id.nav_history);

        navigationIcon.setOnClickListener(this::openNavigationMenu);


    }

    public void openNavigationMenu(View view) {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void openArtistPage(View view) {
        Intent intent = new Intent(this, SelectArtist.class);
        startActivity(intent);
    }
}