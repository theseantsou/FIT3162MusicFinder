package com.example.musicfinder.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.musicfinder.pages.FavouritePage;
import com.example.musicfinder.pages.HistoryPage;
import com.example.musicfinder.LimitButtonClickOnce;
import com.example.musicfinder.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtil {

    public static final int REQUEST_CODE_SELECT_ARTIST = 1001;
    private static final List<String> filters = new ArrayList<>();

    private static int AmountOfSongs = 0;

    public static void setAmountOfSongs(int Amt) {
        AmountOfSongs = Amt;
    }

    public static int getAmountOfSongs() {
        return AmountOfSongs;
    }

    public static void addFilter(String filter) {
        filters.add(filter);
    }

    public static void removeFilter(String filter) {
        filters.remove(filter);
    }

    public static void emptyFilter() { filters.clear(); }

    public static List<String> getFilters() {
        return filters;
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

    public static boolean isPageNotLoading(ProgressBar loadingAnim) {
        return loadingAnim.getVisibility() == View.INVISIBLE;
    }

    public static boolean hasNoInternetWarning(TextView noInternetText) {
        return noInternetText.getVisibility() == View.INVISIBLE;
    }

    public static String getEmailFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String spotifyEmail = sharedPreferences.getString("email", null);

        if (spotifyEmail != null) {
            return spotifyEmail;
        }
        else {
            return "";
        }

    }

    public static void setEmailInSharedPref(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }




}
