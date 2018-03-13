package com.example.oyun.cse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        intent = getIntent();
        String url = intent.getExtras().getString("url");

        webView = (WebView) findViewById(R.id.contentdisplay);
        webView.loadUrl(url);
    }
}
