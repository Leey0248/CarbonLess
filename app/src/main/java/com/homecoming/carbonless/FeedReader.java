package com.homecoming.carbonless;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FeedReader extends AppCompatActivity {

    int FootprintStatus; // 0 = good, 1 = bad, 2 = very bad
    ImageView Back;
    View NavigationView;
    WebView WebView;
    ConstraintLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed_reader);

        main =findViewById(R.id.main);
        NavigationView = findViewById(R.id.NavigationView);
        Back = findViewById(R.id.back);
        Back.setOnClickListener(v -> {
            startActivity(new Intent(FeedReader.this, MainActivity.class));
            //getOnBackPressedDispatcher().onBackPressed();
        });

        WebView = findViewById(R.id.WebView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Configure WebView Settings
        WebSettings webSettings = WebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // --- DISABLE ZOOM ---
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        // --- HANDLE HYPERLINKS ---
        WebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Check if the URL is the one we want to display or an external link
                // In this case, we send ALL clicks to the system browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true; // Tells the WebView NOT to load the URL
            }
        });

        // Load content
        String ContentURL = getIntent().getStringExtra("ContentURL");
        if (ContentURL != null) {
            WebView.loadUrl(ContentURL);
        } else {
            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "@font-face {" +
                    "    font-family: 'ArialRoundedMtBold';" +
                    "    src: url('file:///android_res/font/font.ttf');" +
                    "}" +
                    "html, body { height: 100%; margin: 0; padding: 0; }" +
                    "body { " +
                    "    display: flex; " +
                    "    flex-direction: column; " +
                    "    justify-content: center; " +
                    "    align-items: center; " +
                    "    text-align: center; " +
                    "}" +
                    "h3 { " +
                    "    font-family: 'ArialRoundedMtBold', sans-serif !important; " + // !important ensures it overrides defaults
                    "    margin-top: 20px; " +
                    "    color: #333; " +
                    "}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<img src='file:///android_res/drawable/error' width='180'>" +
                    "<h3>Unable to load content: URL not found.</h3>" +
                    "</body>" +
                    "</html>";

            WebView.loadDataWithBaseURL("file:///android_res/", htmlContent, "text/html", "UTF-8", null);
        }

        registerReceiver(
                LoadReceiver,
                new IntentFilter("com.homecoming.carbonless.onDataLoaded"),
                Context.RECEIVER_NOT_EXPORTED
        );
        UDD.StartLoadingData(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(LoadReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered or already unregistered
            Log.e("LoadReceiver_OnPause", "Receiver not registered: " + e.getMessage());
        }
    }
    private final BroadcastReceiver LoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };
    public void loadData() {
        FootprintStatus = UDD.GetDailyFootprintStatus();
        setUiColor();
    }
    public void setUiColor() {
        if (FootprintStatus == 0) { // Good
            NavigationView.setBackgroundColor(getResources().getColor(R.color.good_bg_color));
            main.setBackgroundColor(getResources().getColor(R.color.good_bg_color));
            Back.setColorFilter(ContextCompat.getColor(this, R.color.good_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (FootprintStatus == 1) { // Bad
            NavigationView.setBackgroundColor(getResources().getColor(R.color.bad_bg_color));
            main.setBackgroundColor(getResources().getColor(R.color.bad_bg_color));
            Back.setColorFilter(ContextCompat.getColor(this, R.color.bad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (FootprintStatus == 2) { // Very Bad
            NavigationView.setBackgroundColor(getResources().getColor(R.color.verybad_bg_color));
            main.setBackgroundColor(getResources().getColor(R.color.verybad_bg_color));
            Back.setColorFilter(ContextCompat.getColor(this, R.color.verybad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}