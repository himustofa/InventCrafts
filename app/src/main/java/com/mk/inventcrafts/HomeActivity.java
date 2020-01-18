package com.mk.inventcrafts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private MyNetworkReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNetworkReceiver = new MyNetworkReceiver(this);
        mProgressBar = findViewById(R.id.reconnecting_progress_bar);
        mWebView = (WebView) findViewById(R.id.web_view);
        startWebView("https://inventcrafts.com");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack())
            mWebView.goBack();
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startWebView(String url) {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        //mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mProgressBar.setVisibility(View.VISIBLE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressBar.isShown()) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.network_unavailable), Snackbar.LENGTH_LONG).show();
                if (mProgressBar.isShown()) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebView.loadUrl(url);
    }
}
