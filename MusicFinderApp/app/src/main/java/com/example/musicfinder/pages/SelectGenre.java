package com.example.musicfinder.pages;

import android.os.Bundle;

public class SelectGenre extends SelectFilter {

    public SelectGenre() {
        super("Genre","It's time to define the genre you're interested in.",SelectPeriod.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}