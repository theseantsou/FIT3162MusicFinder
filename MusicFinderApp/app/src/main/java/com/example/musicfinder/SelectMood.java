package com.example.musicfinder;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collection;
import java.util.Collections;

public class SelectMood extends SelectFilter {

    public SelectMood() {
        super("Occasion or Mood", "Customise your playlist for specific situations or emotional states.", SelectGenre.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}