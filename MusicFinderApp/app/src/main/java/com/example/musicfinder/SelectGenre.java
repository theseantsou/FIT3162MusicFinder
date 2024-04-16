package com.example.musicfinder;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SelectGenre extends SelectFilter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPageName("Genre");
        this.setPageDescription("It's time to define the genre you're interested in.");
        this.setNextPage(SelectPeriod.class);
    }
}