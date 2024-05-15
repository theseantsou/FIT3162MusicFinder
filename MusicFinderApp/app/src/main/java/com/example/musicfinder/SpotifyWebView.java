package com.example.musicfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicfinder.pages.SelectMood;
import com.example.musicfinder.utils.ActivityUtil;
import com.example.musicfinder.utils.BackendHelper;


public class SpotifyWebView extends WebView {
    private Activity parentActivity;
    public SpotifyWebView(@NonNull Context context) {
        super(context);
        initialize((Activity) context);
    }

    public SpotifyWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize((Activity) context);
    }

    public SpotifyWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize((Activity) context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize(Activity parentActivity) {
        SpotifyWebViewClient client = new SpotifyWebViewClient();
        setWebViewClient(client);
        this.parentActivity = parentActivity;

        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private class SpotifyWebViewClient extends WebViewClient {

        String callbackURL = BackendHelper.baseURL + "/callback";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            String redirectedUrl = request.getUrl().toString();
            System.out.println(redirectedUrl);
            // Check if the redirected URL is starting with the callback URL
            if (redirectedUrl.startsWith(callbackURL)) {

                Uri uri = Uri.parse(redirectedUrl);
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    Thread thread = new Thread(() -> {
                        String email = BackendHelper.sendSpotifySessionParams(redirectedUrl);
                        if (!email.isEmpty())
                            ActivityUtil.setEmailInSharedPref(parentActivity, email);
                    });

                    thread.start();
                    try {
                        thread.join();
                        Toast.makeText(parentActivity, "Successfully logged in.", Toast.LENGTH_SHORT).show();
                        parentActivity.setResult(ActivityUtil.RESULT_CODE_SUCCESSFUL_LOGIN);
                    }
                    catch (Exception e) {
                        Toast.makeText(parentActivity, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show();
                        parentActivity.setResult(ActivityUtil.RESULT_CODE_FAILED_LOGIN);
                    }


                } else {
                    Toast.makeText(parentActivity, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show();
                    parentActivity.setResult(ActivityUtil.RESULT_CODE_FAILED_LOGIN);
                }

                parentActivity.finish();
                return true;



            }

            return super.shouldOverrideUrlLoading(view, request);
        }
    }



}
