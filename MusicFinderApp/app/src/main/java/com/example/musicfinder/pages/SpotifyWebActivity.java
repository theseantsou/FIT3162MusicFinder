package com.example.musicfinder.pages;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicfinder.R;
import com.example.musicfinder.SpotifyWebView;
import com.example.musicfinder.utils.ActivityUtil;

public class SpotifyWebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spotify_web);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String url = getIntent().getStringExtra("url");
        FrameLayout layout = findViewById(R.id.main);

        SpotifyWebView webView = new SpotifyWebView(this);
        if (url != null)
            webView.loadUrl(url);

        layout.addView(webView);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(ActivityUtil.REQUEST_CODE_SELECT_ARTIST);
                finish();
            }
        });
    }
}