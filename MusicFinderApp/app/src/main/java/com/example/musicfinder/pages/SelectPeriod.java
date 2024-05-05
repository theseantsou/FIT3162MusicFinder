package com.example.musicfinder.pages;

import android.os.Bundle;


public class SelectPeriod extends SelectFilter{

    public SelectPeriod() {
        super("Time Period (Decade)", "Specify the time period you prefer.", SelectArtist.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}