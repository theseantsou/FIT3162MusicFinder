package com.example.musicfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SelectFilter extends AppCompatActivity implements LimitButtonClickOnce {
    private int count;

    private boolean isButtonClickable = true;

    // Create constant list of page title names
    private List<String> PAGE_NAMES;

    // Create constant list of page descriptions
    private List<String> PAGE_DESCRIPTIONS;

    // Create constant list of edit text hints
    private List<String> PAGE_TEXT_HINTS;
    private EditText editTextView;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    public void setButtonClickable(boolean buttonClickable) {
        isButtonClickable = buttonClickable;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_filter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // List of pages name, description, text hints since the filter page is similar
        // Don't need to open separate instance
        PAGE_NAMES = Collections.unmodifiableList(
                Arrays.asList(getResources().getStringArray(R.array.title_name))
        );
        PAGE_DESCRIPTIONS = Collections.unmodifiableList(
                Arrays.asList(getResources().getStringArray(R.array.page_desc))
        );
        PAGE_TEXT_HINTS = Collections.unmodifiableList(
                Arrays.asList(getResources().getStringArray(R.array.page_hint))
        );

        count = 0;
        editTextView = findViewById(R.id.editTextText1);
        setTextView(count);

        ActivityUtil.setNavigationDrawerEvents(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closePage();
            }
        });

        launcher = ActivityUtil.getResultLauncher(this);

        View backImage = findViewById(R.id.imageViewBack);
        backImage.setOnClickListener(v->closePage());

        View nextButton = findViewById(R.id.textViewNext);
        View skipButton = findViewById(R.id.textViewSkip);

        Stream.of(nextButton, skipButton)
                .forEach(b->b.setOnClickListener(v->openNextPage()));

<<<<<<< Updated upstream
        launcher = ActivityUtil.getResultLauncher(this);
=======


>>>>>>> Stashed changes
    }

    public void closePage() {
        editTextView.setText("");
        if (count == 0) {
            setResult(ActivityUtil.REQUEST_CODE_SELECT_ARTIST);
            finish();
        }
        else {
            count -= 1;
            setTextView(count);
        }
    }


    /**
     * Button click event to open next page depending on the count
     */
    public void openNextPage() {
        count += 1;
        editTextView.setText("");
        if (count == PAGE_NAMES.size()) {
            count -= 1;
            // open the page where prompts user to input amount of songs
            Intent intent = new Intent(this, SelectMusicNum.class);
            launcher.launch(intent);
        }
        else {
            // TODO: store the info on the database or locally to parse to LLM
            setTextView(count);
        }

    }

    /**
     * Function that sets the TextView and EditView based on the page currently on
     * @param count the page number currently on
     */
    public void setTextView(int count) {
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewDesc = findViewById(R.id.textViewDesc);


        textViewTitle.setText(PAGE_NAMES.get(count));
        textViewDesc.setText(PAGE_DESCRIPTIONS.get(count));
        editTextView.setHint(PAGE_TEXT_HINTS.get(count));
    }



}