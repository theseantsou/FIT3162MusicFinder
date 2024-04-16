package com.example.musicfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class SelectFilter extends AppCompatActivity implements LimitButtonClickOnce {
    private boolean isButtonClickable;
    private TextView pageName;
    private TextView pageDescription;

    private Class<? extends AppCompatActivity> nextPage;

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

        List<AppCompatButton> buttonList = new ArrayList<>();
        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5}; // IDs of your buttons

        for (int id : buttonIds) {
            AppCompatButton button = findViewById(id);
            buttonList.add(button);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> response = BackendHelper.requestFilters(pageName.getText().toString(), Collections.emptyList());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < response.size(); i++) {
                            buttonList.get(i).setText(response.get(i));
                        }
                    }
                });
            }


        }).start();

    }

    public void setPageName(String pageName) {
        this.pageName = (TextView) findViewById(R.id.textViewTitle);
        this.pageName.setText(pageName);
    }

    public void setPageDescription(String pageDescription) {
        this.pageDescription = (TextView) findViewById(R.id.textViewDesc);
        this.pageDescription.setText(pageDescription);
    }

    public void setNextPage(Class<? extends AppCompatActivity> nextPage) {
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