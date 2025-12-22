package com.homecoming.carbonless;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.mediapipe.tasks.genai.llminference.LlmInference;
import java.io.File;

public class CarbonFootprintGeneral extends AppCompatActivity {

    private static final String TAG = "CarbonFootprintGeneral_LLM_Debug";
    StringBuilder ChatString;
    int FootprintStatus = 0;
    ImageView Back, Settings;
    TextView CarbnFootprint, Suggestions;
    ConstraintLayout main;
    private LlmInference llmInference;
    //private final String MODEL_URL = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/Gemma3-1B-IT_multi-prefill-seq_q4_block128_ekv1280.task";
    private final String MODEL_URL = "https://drive.usercontent.google.com/download?id=1m-hzkBtQLfK1FB4xxwlvRWqFLZljD6DZ&export=download&confirm=t&uuid=12241e88-307e-45db-8d64-27cf7d72ff41";
    private final String MODEL_FILE_NAME = "gemma3.task";
    private long downloadID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carbon_footprint_general);

        // Initialize Views
        Back = findViewById(R.id.back);
        Settings = findViewById(R.id.settings);
        CarbnFootprint = findViewById(R.id.CarbnFootprint);
        Suggestions = findViewById(R.id.Suggestions);
        main = findViewById(R.id.main);

        ChatString = new StringBuilder("AI Suggestion:\n");


        Back.setOnClickListener(v -> startActivity(new Intent(CarbonFootprintGeneral.this, MainActivity.class)));

        setUiColor();
        checkAndPrepareModel();

        new CountDownTimer(1000, 100) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                String input = "Hello";
                if (!input.isEmpty() && llmInference != null) {
                    generateResponse(input);
                } else if (llmInference == null) {
                    Suggestions.setText(ChatString + "AI is still loading...");
                }
            }
        }.start();
    }

    private void checkAndPrepareModel() {
        File modelFile = new File(getExternalFilesDir(null), MODEL_FILE_NAME);
        if (modelFile.exists()) {
            Log.d(TAG, "Model found locally. Initializing...");
            initLlm(modelFile.getAbsolutePath());
        } else {
            Log.d(TAG, "Model not found. Starting download.");
            Suggestions.setText("Downloading model...");
            startModelDownload();
        }
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                DownloadManager query = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(id);
                android.database.Cursor c = query.query(q);

                if (c.moveToFirst()) {
                    @SuppressLint("Range") int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        File downloadedFile = new File(getExternalFilesDir(null), MODEL_FILE_NAME);
                        initLlm(downloadedFile.getAbsolutePath());
                    } else {
                        // This will tell you the exact error (e.g., 403, 404, 500)
                        @SuppressLint("Range") int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                        Log.e("AIChat_Debug", "Download failed! Reason code: " + reason);
                        runOnUiThread(() -> Suggestions.setText("Download failed. Error Code: " + reason));
                    }
                }
                c.close();
            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void startModelDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        // UPDATED URL: Sometimes 'main' branch needs to be explicit or 'resolve' is picky.
        // Ensure you are using the exact link from the "Download" button on HF.
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(MODEL_URL))
                .setTitle("Downloading Gemma 3")
                .setDescription("Preparing AI chat...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(this, null, MODEL_FILE_NAME)
                // ADD THESE TWO HEADERS:
                //.addRequestHeader("Authorization", "Bearer hf_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .addRequestHeader("User-Agent", "Mozilla/5.0 (Android)");

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(request);
            Log.d("AIChat_Debug", "Download enqueued. ID: " + downloadID);
        }
    }

    private void initLlm(String path) {
        runOnUiThread(() -> Suggestions.setText(ChatString + "Loading model..."));

        new Thread(() -> {
            try {
                // FIXED: Removed setResultListener from builder (it goes in generateResponseAsync)
                LlmInference.LlmInferenceOptions options = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(path)
                        .setMaxTokens(1024)
                        .build();

                llmInference = LlmInference.createFromOptions(this, options);
                runOnUiThread(() -> Suggestions.setText(ChatString + "Loading..."));
                Log.d(TAG, "LlmInference created successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to init LLM", e);
                runOnUiThread(() -> Suggestions.setText("Init Error: " + e.getMessage()));
            }
        }).start();
    }

    private void generateResponse(String prompt) {
        if (llmInference == null) return;

        Suggestions.setText(ChatString + "Loading...");

        llmInference.generateResponseAsync(prompt, (result, done) -> {
            runOnUiThread(() -> {
                String partialText = String.valueOf(result);

                ChatString.append(partialText);

                // --- MARKDOWN TO HTML CONVERSION ---
                // Replace **text** with <b>text</b>
                String formattedText = ChatString.toString().replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

                // Handle line breaks (HTML needs <br> instead of \n)
                formattedText = formattedText.replace("\n", "<br>");

                // Set the text using fromHtml
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Suggestions.setText(android.text.Html.fromHtml(formattedText, android.text.Html.FROM_HTML_MODE_LEGACY));
                } else {
                    Suggestions.setText(android.text.Html.fromHtml(formattedText));
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(onDownloadComplete); } catch (Exception ignored) {}
        if (llmInference != null) llmInference.close();
    }

    public void setUiColor() {
        if (FootprintStatus == 0) { // Good
            Suggestions.setTextColor(getResources().getColor(R.color.good_fg_color));
            Suggestions.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            main.setBackgroundColor(getResources().getColor(R.color.good_bg_color));
        } else if (FootprintStatus == 1) { // Bad
            Suggestions.setTextColor(getResources().getColor(R.color.bad_fg_color));
            Suggestions.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.bad_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            main.setBackgroundColor(getResources().getColor(R.color.bad_bg_color));
        } else if (FootprintStatus == 2) { // Very Bad
            Suggestions.setTextColor(getResources().getColor(R.color.verybad_fg_color));
            Suggestions.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.verybad_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            main.setBackgroundColor(getResources().getColor(R.color.verybad_bg_color));
        }
    }
}