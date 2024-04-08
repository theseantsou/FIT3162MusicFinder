package com.example.musicfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.stream.Stream;

public abstract class SelectFilter extends AppCompatActivity implements LimitButtonClickOnce {
    private boolean isButtonClickable;
    private TextView pageName;
    private TextView pageDescription;

    private Class<?> nextPage;

    private ActivityResultLauncher<Intent> launcher;

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
    }

    public void setPageName(String pageName) {
        this.pageName = (TextView) findViewById(R.id.textViewTitle);
        this.pageName.setText(pageName);
    }

    public void setPageDescription(String pageDescription) {
        this.pageDescription = (TextView) findViewById(R.id.textViewDesc);
        this.pageDescription.setText(pageDescription);
    }

    public void setNextPage(Class<?> nextPage) {
        this.nextPage = nextPage;
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