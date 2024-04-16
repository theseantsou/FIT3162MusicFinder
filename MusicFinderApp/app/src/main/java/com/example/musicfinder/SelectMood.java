package com.example.musicfinder;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collection;
import java.util.Collections;

public class SelectMood extends SelectFilter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPageName("Occasion or Mood");
        this.setPageDescription("Customise your playlist for specific situations or emotional states.");
        this.setNextPage(SelectGenre.class);

    }
}