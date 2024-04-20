package com.example.musicfinder;

import android.os.Bundle;


public class SelectPeriod extends SelectFilter{

    public SelectPeriod() {
        super("Time Period", "Specify the time period you prefer.", SelectArtist.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}