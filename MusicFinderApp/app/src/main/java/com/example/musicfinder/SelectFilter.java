package com.example.musicfinder;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class SelectFilter extends AppCompatActivity implements LimitButtonClickOnce {
    private boolean isButtonClickable;
    private String pageName;
    private String pageDescription;
    private Class<? extends AppCompatActivity> nextPage;

    private ActivityResultLauncher<Intent> launcher;

    private List<AppCompatButton> buttonList;

    public SelectFilter(String pageTitle, String pageDesc, Class<? extends AppCompatActivity> nextPage) {
        pageName = pageTitle;
        pageDescription = pageDesc;
        this.nextPage = nextPage;
    }

    @Override
    public void setButtonClickable(boolean buttonClickable) {
        this.isButtonClickable = buttonClickable;
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

        this.isButtonClickable = true;

        ActivityUtil.setNavigationDrawerEvents(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closePage();
            }
        });

        this.launcher = ActivityUtil.getResultLauncher(this);

        View backImage = findViewById(R.id.imageViewBack);
        backImage.setOnClickListener(v->closePage());

        View nextButton = findViewById(R.id.textViewNext);
        View skipButton = findViewById(R.id.textViewSkip);

        Stream.of(nextButton, skipButton)
                .forEach(b->b.setOnClickListener(v->openNextPage()));

        this.launcher = ActivityUtil.getResultLauncher(this);

        setPageProperties();

        buttonList = new ArrayList<>();
        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5}; // IDs of your buttons

        for (int id : buttonIds) {
            AppCompatButton button = findViewById(id);
            buttonList.add(button);
        }

        populateButtons();

        for (AppCompatButton button : buttonList) {
            button.setOnClickListener(v->recommendationButtonClicked((AppCompatButton) v));
        }
    }

    public void recommendationButtonClicked(AppCompatButton button) {
        int currentTextColor = button.getCurrentTextColor();

        int textColor;
        Drawable backgroundDrawable;

        boolean isCurrentlySelected = currentTextColor == Color.WHITE;

        textColor = isCurrentlySelected ? Color.BLACK : Color.WHITE;
        backgroundDrawable = isCurrentlySelected ?
                ContextCompat.getDrawable(this, R.drawable.white_button) :
                ContextCompat.getDrawable(this, R.drawable.black_button);

        if (isCurrentlySelected) {
            // Unselect button (remove from previous filters)
            ActivityUtil.removeFilter(button.getText().toString());
        }
        else {
            ActivityUtil.addFilter(button.getText().toString());
        }


        button.setBackgroundDrawable(backgroundDrawable);
        button.setTextColor(textColor);

    }

    public void populateButtons() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> response = BackendHelper.requestFilters(pageName, ActivityUtil.getPreviousFilter());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < buttonList.size(); i++) {
                            buttonList.get(i).setText(response.get(i));
                        }
                    }
                });
            }


        }).start();
    }

    public void setPageProperties() {
        TextView pageNameView = findViewById(R.id.textViewTitle);
        pageNameView.setText(this.pageName);

        TextView pageDescView = findViewById(R.id.textViewDesc);
        pageDescView.setText(this.pageDescription);
    }

    public void closePage() {
        setResult(ActivityUtil.REQUEST_CODE_SELECT_ARTIST);
        finish();

    }

    /**
     * Button click event to open next page depending on the count
     */
    public void openNextPage() {
        if (isButtonClickable) {
            // open the page where prompts user to input amount of songs
            Intent intent = new Intent(this, nextPage);
            launcher.launch(intent);
            setButtonClickable(false);
        }

    }

}