package com.example.musicfinder.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicfinder.LimitButtonClickOnce;
import com.example.musicfinder.R;
import com.example.musicfinder.utils.ActivityUtil;

public class SelectMusicNum extends AppCompatActivity implements LimitButtonClickOnce {
    private boolean isButtonClickable = true;

    @Override
    public void setButtonClickable(boolean buttonClickable) {
        isButtonClickable = buttonClickable;
    }

    private NumberPicker numberPicker;
    private final String[] displayedValues = {"10", "20", "30", "40", "50"};
    private ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_music_num);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        numberPicker = findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(displayedValues.length - 1);
        numberPicker.setDisplayedValues(displayedValues);

        ActivityUtil.setNavigationDrawerEvents(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                openParentPage();
            }
        });

        View backImage = findViewById(R.id.backImageView);
        backImage.setOnClickListener(v -> openParentPage());

        launcher = ActivityUtil.getResultLauncher(this);

        View generatePlaylistButton = findViewById(R.id.buttonGenerate);
        generatePlaylistButton.setOnClickListener(v -> openPlaylistPage());
    }

    public void openParentPage() {
        setResult(ActivityUtil.REQUEST_CODE_SELECT_ARTIST);
        finish();
    }

    public void openPlaylistPage() {
        if (isButtonClickable) {
            isButtonClickable = false;
            Intent intent = new Intent(this, GeneratePlaylist.class);
            ActivityUtil.setAmountOfSongs(Integer.parseInt(displayedValues[numberPicker.getValue()]));
            launcher.launch(intent);
        }


    }


}