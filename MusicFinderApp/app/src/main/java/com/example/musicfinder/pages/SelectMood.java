package com.example.musicfinder.pages;

import android.os.Bundle;

public class SelectMood extends SelectFilter {

    public SelectMood() {
        super("Occasion or Mood", "Customise your playlist for specific situations or emotional states.", SelectGenre.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}