package com.example.musicfinder;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SelectGenre extends SelectFilter {

    public SelectGenre() {
        super("Genre","It's time to define the genre you're interested in.",SelectPeriod.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}