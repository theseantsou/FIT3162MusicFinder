package com.example.musicfinder;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtil {

    public static final int REQUEST_CODE_SELECT_ARTIST = 1001;
    private static final List<String> previousFilter = new ArrayList<>();

    public static void addFilter(String filter) {
        previousFilter.add(filter);
        printFilters();
    }

    public static void removeFilter(String filter) {
        previousFilter.remove(filter);
        printFilters();
    }

    public static List<String> getPreviousFilter() {
        return previousFilter;
    }

    public static void printFilters() {
        for (String filter : previousFilter) {
            System.out.println(filter);
        }
    }
    public static void setNavigationDrawerEvents(AppCompatActivity activity) {
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        ImageView navigationIcon = activity.findViewById(R.id.imageMenu);
        navigationView.bringToFront();
        View headerView = navigationView.getHeaderView(0);

        if (headerView != null) {
            ImageView closeImg = headerView.findViewById(R.id.closeImage);
            closeImg.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        }


        navigationIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView, true);
                } else {
                    // If the drawer is closed, let the system handle the back button press
                    if (isEnabled()) {
                        setEnabled(false);
                        activity.getOnBackPressedDispatcher().onBackPressed();
                    }
                }
            }
        };

        activity.getOnBackPressedDispatcher().addCallback(activity, callback);

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation item clicks here
            // You can also pass the activity instance to handle the item clicks
            drawerLayout.closeDrawer(GravityCompat.END);
            return handleNavigationItemSelected(activity, item);
        });
    }

    private static boolean handleNavigationItemSelected(AppCompatActivity activity, MenuItem item) {
        // Handle the navigation item click here
        // You can switch between different activities based on the clicked item
        // Example:

        if (activity instanceof HistoryPage || activity instanceof FavouritePage) {
            activity.finish();
        }
        if (item.getItemId() == R.id.nav_fav) {
            activity.startActivity(new Intent(activity, FavouritePage.class));
            if (activity instanceof FavouritePage) {
                activity.overridePendingTransition(0, 0);
            }
        }

        else if (item.getItemId() == R.id.nav_history) {
            activity.startActivity(new Intent(activity, HistoryPage.class));
            if (activity instanceof HistoryPage) {
                activity.overridePendingTransition(0, 0);
            }

        }

        return true;
    }

    public static ActivityResultLauncher<Intent> getResultLauncher(AppCompatActivity activity) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == REQUEST_CODE_SELECT_ARTIST) {
                        ((LimitButtonClickOnce) activity).setButtonClickable(true);
                    }
                }
        );
    }




}
