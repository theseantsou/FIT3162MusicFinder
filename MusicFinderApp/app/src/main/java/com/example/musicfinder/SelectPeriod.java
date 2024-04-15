package com.example.musicfinder;

import android.os.Bundle;


public class SelectPeriod extends SelectFilter{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPageName("Time Period");
        this.setPageDescription("Specify the time period you prefer.");
        this.setNextPage(SelectArtist.class);
    }
}