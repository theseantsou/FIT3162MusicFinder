package com.example.musicfinder.pages;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(SpotifyWebActivity.this, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show();
                setResult(ActivityUtil.RESULT_CODE_FAILED_LOGIN);
                finish();
            }
        });


        String url = getIntent().getStringExtra("url");
        FrameLayout layout = findViewById(R.id.main);

        SpotifyWebView webView = new SpotifyWebView(this);
        if (url != null)
            webView.loadUrl(url);

        layout.addView(webView);
    }


}