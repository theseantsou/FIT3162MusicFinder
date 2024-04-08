package com.example.musicfinder;

import android.os.Bundle;

public class SelectMood extends SelectFilter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPageName("Occasion or Mood");
        this.setPageDescription("Customise your playlist for specific situations or emotional states.");
        this.setNextPage(SelectGenre.class);
    }
}