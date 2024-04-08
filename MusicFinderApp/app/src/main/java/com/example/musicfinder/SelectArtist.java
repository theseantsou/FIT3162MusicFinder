package com.example.musicfinder;

import android.os.Bundle;


public class SelectArtist extends SelectFilter{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPageName("Artist");
        this.setPageDescription("Continue your music journey by selecting a specific artist or band.");
        this.setNextPage(SelectMusicNum.class);

    }
}