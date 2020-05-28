package com.example.dualscreenwebviewsample;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private ScreenHelper screenHelper;
    private boolean isDuo;

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenHelper = new ScreenHelper();
        isDuo = screenHelper.initialize(this);

        webView = findViewById(R.id.webView);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest (final WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("assets".equals(uri.getHost())) {
                    try {
                        String path = uri.toString().substring(15);
                        String query = uri.getQuery();
                        if(query != null) {
                            path = path.substring(0, path.length() - query.length() - 1);
                        }
                        return new WebResourceResponse(
                                URLConnection.guessContentTypeFromName(uri.getPath()),
                                "utf-8",
                                MainActivity.this.getAssets().open(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });

        webView.loadUrl("https://assets/index.html");
        //webView.loadUrl("https://foldable-devices.github.io/demos/battleship/");

        UpdateSpanning();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        UpdateSpanning();
    }

    private void UpdateSpanning()
    {
        int rotation = ScreenHelper.getRotation(this);

        String spanning = "none";
        int foldSize = 0;

        if (isDuo && screenHelper.isDualMode()) {
            Rect hinge = screenHelper.getHinge(rotation);
            switch (rotation) {
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    foldSize = hinge.height();
                    spanning = "single-fold-horizontal";
                    break;
                default:
                    foldSize = hinge.width();
                    spanning = "single-fold-vertical";
                    break;
            }
        }

        webView.evaluateJavascript(String.format("(window[\"__foldables_env_vars__\"]).update({spanning: '%1$s', foldSize: %2$s})", spanning, foldSize), null);
    }
}
