package com.example.musicfinder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.stream.Stream;

public class SelectMood extends SelectFilter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPageName("Mood");
        this.setPageDescription("Start your music journey by telling us how you are feeling");
        this.setNextPage(SelectGenre.class);
    }
}