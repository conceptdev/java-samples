package com.example.dualscreenwebviewdemo;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
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
        hideSystemUI();
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
        // TODO: Uncomment the line below to instead view the boxes demo - https://github.com/MicrosoftEdge/MSEdgeExplainers/blob/master/Foldables/colored-boxes.svg
        //webView.loadUrl("https://assets/boxes.html");

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
                    foldSize = (int) (hinge.height() / 2.5); // height is returned in pixels, CSS wants in dp
                    spanning = "single-fold-horizontal";
                    break;
                default:
                    foldSize = (int) (hinge.width() / 2.5); // height is returned in pixels, CSS wants in dp
                    spanning = "single-fold-vertical";
                    break;
            }
        }

        webView.evaluateJavascript(String.format("(window[\"__foldables_env_vars__\"]).update({spanning: '%1$s', foldSize: %2$s})", spanning, foldSize), null);
    }

    // https://developer.android.com/training/system-ui/immersive
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
