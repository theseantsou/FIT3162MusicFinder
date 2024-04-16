package com.example.musicfinder;

import android.os.Bundle;


public class SelectArtist extends SelectFilter{

    public SelectArtist() {
        super("Artist", "Continue your music journey by selecting a specific artist or band.", SelectMusicNum.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}