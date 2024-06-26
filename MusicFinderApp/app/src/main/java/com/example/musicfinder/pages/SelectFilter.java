package com.example.musicfinder.pages;



import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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

import com.example.musicfinder.LimitButtonClickOnce;
import com.example.musicfinder.R;
import com.example.musicfinder.utils.ActivityUtil;
import com.example.musicfinder.utils.BackendHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class SelectFilter extends AppCompatActivity implements LimitButtonClickOnce {
    private boolean isButtonClickable;
    private final String pageName;
    private final String pageDescription;
    private final Class<? extends AppCompatActivity> nextPage;

    private ActivityResultLauncher<Intent> launcher;

    private List<AppCompatButton> buttonList;
    private List<Boolean> buttonActivatedList;

    private ProgressBar loadingAnim;
    private TextView noInternetText;

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
        nextButton.setOnClickListener(v->openNextPage());

        View regenButton = findViewById(R.id.textViewRegen);
        regenButton.setOnClickListener(v->populateButtons());

        this.launcher = ActivityUtil.getResultLauncher(this);

        setPageProperties();

        loadingAnim = findViewById(R.id.progressBar);
        noInternetText = findViewById(R.id.textViewNoInternet);

        initializeButtons();

        populateButtons();

        for (AppCompatButton button : buttonList) {
            button.setOnClickListener(v->recommendationButtonClicked((AppCompatButton) v));
        }
    }

    public void recommendationButtonClicked(AppCompatButton button) {
        int buttonIndex = buttonList.indexOf(button);
        if (buttonIndex != -1 && ActivityUtil.isPageNotLoading(loadingAnim)) {
            int textColor;
            Drawable backgroundDrawable;

            boolean isCurrentlySelected = buttonActivatedList.get(buttonIndex);

            textColor = isCurrentlySelected ? Color.BLACK : Color.WHITE;
            backgroundDrawable = isCurrentlySelected ?
                    ContextCompat.getDrawable(this, R.drawable.white_button) :
                    ContextCompat.getDrawable(this, R.drawable.black_button);

            if (isCurrentlySelected) {
                ActivityUtil.removeFilter(button.getText().toString());
            }
            else {
                ActivityUtil.addFilter(button.getText().toString());
            }

            buttonActivatedList.set(buttonIndex, !buttonActivatedList.get(buttonIndex));
            button.setBackgroundDrawable(backgroundDrawable);
            button.setTextColor(textColor);
        }


    }

    public void initializeButtons() {
        buttonList = new ArrayList<>();
        buttonActivatedList = new ArrayList<>();
        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5};

        for (int id : buttonIds) {
            AppCompatButton button = findViewById(id);
            buttonList.add(button);
        }

        for (int i = 0; i < buttonList.size(); i++)  {
            buttonActivatedList.add(false);
        }
    }

    public void populateButtons() {
        if (ActivityUtil.isPageNotLoading(loadingAnim) && !getUnselectedButtons().isEmpty()) {
            hideButtons();
            loadingAnim.setVisibility(View.VISIBLE);
            noInternetText.setVisibility(View.INVISIBLE);
            new Thread(() -> {
                int filterAmt = getUnselectedButtons().size();
                List<String> prevResponse = IntStream.range(0, buttonList.size())
                        .filter(i -> !buttonList.get(i).getText().toString().isEmpty() && !buttonActivatedList.get(i))
                        .mapToObj(i -> buttonList.get(i).getText().toString())
                        .collect(Collectors.toList());
                
                List<String> response = BackendHelper.requestFilters(filterAmt, pageName, ActivityUtil.getFilters(), prevResponse);
                runOnUiThread(() -> {
                    if (response != null) {
                        int count = 0;
                        for (int i = 0; i < buttonList.size(); i++) {
                            if (!buttonActivatedList.get(i)) {

                                buttonList.get(i).setText(response.get(count));
                                count++;
                            }
                        }
                        showButtons();
                        loadingAnim.setVisibility(View.INVISIBLE);
                    }
                    else {
                        loadingAnim.setVisibility(View.INVISIBLE);
                        noInternetText.setVisibility(View.VISIBLE);
                    }
                });

            }).start();
        }

    }

    public void showButtons() {
        IntStream.range(0, buttonList.size())
                .forEach(index -> {
                    if (!buttonActivatedList.get(index)) {
                        AppCompatButton button = buttonList.get(index);
                        button.setAlpha(0f);
                        button.setVisibility(View.VISIBLE);
                        button.animate().alpha(1f).setDuration(500).setListener(null);
                    }
                });

    }

    public void hideButtons() {
        IntStream.range(0, buttonList.size())
                        .forEach(index -> {
                            if (!buttonActivatedList.get(index)) {
                                AppCompatButton button = buttonList.get(index);
                                button.setVisibility(View.INVISIBLE);
                            }
                        });

    }

    public List<AppCompatButton> getUnselectedButtons() {

        return IntStream.range(0, buttonList.size())
                .filter(i -> !buttonActivatedList.get(i))
                .mapToObj(buttonList::get)
                .collect(Collectors.toList());
    }
    public void setPageProperties() {
        TextView pageNameView = findViewById(R.id.textViewTitle);
        pageNameView.setText(this.pageName);

        TextView pageDescView = findViewById(R.id.textViewDesc);
        pageDescView.setText(this.pageDescription);
    }

    public void closePage() {
        if (ActivityUtil.isPageNotLoading(loadingAnim)) {
            getUnselectedButtons().forEach(button->ActivityUtil.removeFilter(button.getText().toString()));
            finish();
        }


    }

    /**
     * Button click event to open next page depending on the count
     */
    public void openNextPage() {
        if (isButtonClickable && ActivityUtil.isPageNotLoading(loadingAnim) && ActivityUtil.hasNoInternetWarning(noInternetText)) {
            // open the page where prompts user to input amount of songs
            Intent intent = new Intent(this, nextPage);
            launcher.launch(intent);
            setButtonClickable(false);
        }

    }



}