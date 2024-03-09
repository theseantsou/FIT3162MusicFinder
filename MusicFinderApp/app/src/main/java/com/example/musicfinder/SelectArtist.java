package com.example.musicfinder;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelectArtist extends AppCompatActivity {
    private int count;

    // Define constant page names
    public static final String PAGE_ARTIST = "Artist";
    public static final String PAGE_GENRE = "Genre";
    public static final String PAGE_TIME_PERIOD = "Time Period";
    public static final String PAGE_MOOD = "Occasion or\nMood";

    public static final String PAGE_ADDITIONAL = "Additional Filters";

    // Define constant page descriptions
    public static final String DESC_ARTIST = "Start your music journey by\nselecting a specific artist or band. ";
    public static final String DESC_GENRE = "it's time to define the genre you're\ninterested in.";
    public static final String DESC_TIME_PERIOD = "Specify the time period you prefer.";
    public static final String DESC_MOOD = "Customise your playlist for specific\nsituations or emotional states. ";
    public static final String DESC_ADDITIONAL = "Additional filters to refine the music\nrecommendations.";

    //  Define constant text view hints
    public static final String HINT_ARTIST = "Enter an artist/band...";
    public static final String HINT_GENRE = "Enter a genre...";
    public static final String HINT_TIME_PERIOD = "Choose a specific decade...";
    public static final String HINT_MOOD = "Describe the occasion/mood...";
    public static final String HINT_ADDITIONAL = "Filters to apply...";


    // Create constant list of page title names
    public static final List<String> PAGE_NAMES = Collections.unmodifiableList(
            Arrays.asList(PAGE_ARTIST, PAGE_GENRE, PAGE_TIME_PERIOD, PAGE_MOOD, PAGE_ADDITIONAL)
    );

    // Create constant list of page descriptions
    public static final List<String> PAGE_DESCRIPTIONS = Collections.unmodifiableList(
            Arrays.asList(DESC_ARTIST, DESC_GENRE, DESC_TIME_PERIOD, DESC_MOOD, DESC_ADDITIONAL)
    );

    // Create constant list of edit text hints
    public static final List<String> PAGE_TEXT_HINTS = Collections.unmodifiableList(
            Arrays.asList(HINT_ARTIST, HINT_GENRE, HINT_TIME_PERIOD, HINT_MOOD, HINT_ADDITIONAL)
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_artist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        count = 0;

        setTextView(count);
    }

    public void openParentPage(View view) {
        if (count == 0) {
            finish();
        }
        else {
            count -= 1;
            setTextView(count);
        }
    }

    /**
     * Button click event to open next page depending on the count
     * @param view UI component that triggers the function
     */
    public void openNextPage(View view) {
        count += 1;

        if (count == PAGE_NAMES.size()) {
            // TODO: open the page where prompts user to input amount of songs


            return;
        }
        else {
            // TODO: store the info on the database or locally to parse to LLM
            setTextView(count);
        }

    }

    public void setTextView(int count) {
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewDesc = findViewById(R.id.textViewDesc);
        EditText editTextHint = findViewById(R.id.editTextText1);

        textViewTitle.setText(PAGE_NAMES.get(count));
        textViewDesc.setText(PAGE_DESCRIPTIONS.get(count));
        editTextHint.setHint(PAGE_TEXT_HINTS.get(count));
    }
}