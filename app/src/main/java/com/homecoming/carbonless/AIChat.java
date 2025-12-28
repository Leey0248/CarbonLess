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
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.mediapipe.tasks.genai.llminference.LlmInference;
import java.io.File;

public class AIChat extends AppCompatActivity {

    private static final String TAG = "AIChat_Debug";
    StringBuilder ChatString;
    int FootprintStatus;
    EditText ChatBox;
    LinearLayout NavigationRow, HomeItem, ChatItem;
    ImageView HomeButton, ChatButton, SendButton;
    TextView HomeText, ChatText, LLMChat;
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
        setContentView(R.layout.activity_aichat);

        // Initialize Views
        ChatBox = findViewById(R.id.ChatBox);
        LLMChat = findViewById(R.id.LlmChat);
        NavigationRow = findViewById(R.id.NavigationRow);
        HomeItem = findViewById(R.id.HomeItem);
        HomeButton = findViewById(R.id.HomeButton);
        HomeText = findViewById(R.id.HomeText);
        ChatItem = findViewById(R.id.ChatItem);
        ChatButton = findViewById(R.id.ChatButton);
        ChatText = findViewById(R.id.ChatText);
        SendButton = findViewById(R.id.SendButton);
        main = findViewById(R.id.main);

        ChatString = new StringBuilder("");

        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            // 1. Handle basic system bar padding (Top/Sides)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);

            // 2. Calculate the keyboard height relative to the navigation bar
            int keyboardHeight = imeInsets.bottom - systemBars.bottom;
            int margin16dp = (int) (16 * getResources().getDisplayMetrics().density);

            if (keyboardHeight > 0) {
                // KEYBOARD OPEN
                // Shift BOTH the ChatBox and the SendButton up.
                // We add the keyboard height PLUS your 16dp margin.
                float moveUpBy = -(keyboardHeight + margin16dp);

                ChatBox.setTranslationY(moveUpBy);
                SendButton.setTranslationY(moveUpBy);

                // Hide the navigation row so it doesn't stay visible behind/under the box
                NavigationRow.setVisibility(View.GONE);
            } else {
                // KEYBOARD CLOSED
                ChatBox.setTranslationY(0);
                SendButton.setTranslationY(0);
                NavigationRow.setVisibility(View.VISIBLE);

                // Add bottom padding so NavRow sits above system buttons
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }

            return WindowInsetsCompat.CONSUMED;
        });

        HomeItem.setOnClickListener(v -> startActivity(new Intent(AIChat.this, MainActivity.class)));

        FootprintStatus = UDD.GetDailyFootprintStatus();
        setUiColor();
        checkAndPrepareModel();

        SendButton.setOnClickListener(v -> {
                String input = ChatBox.getText().toString().trim();
                if (!input.isEmpty() && llmInference != null) {
                    generateResponse(input);
                    if (ChatString.length() == 0) {
                        ChatString.append("You:\n" + input);
                    } else {
                        ChatString.append("\n\nYou:\n" + input);
                    }
                    String userFormatted = ChatString.toString().replace("\n", "<br>").replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
                    LLMChat.setText(android.text.Html.fromHtml(userFormatted, android.text.Html.FROM_HTML_MODE_LEGACY));
                    ChatBox.setText("");
                } else if (llmInference == null) {
                    LLMChat.setText("AI is still loading...");
                }
        });

        // Making the textbox to be at the top of the keyboard
        ScrollView scrollView = findViewById(R.id.ScrollView);
        EditText chatBox = findViewById(R.id.ChatBox);
        chatBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Use postDelayed to wait for the keyboard animation to finish
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 300);
            }
        });
    }

    private void checkAndPrepareModel() {
        File modelFile = new File(getExternalFilesDir(null), MODEL_FILE_NAME);
        if (modelFile.exists()) {
            Log.d(TAG, "Model found locally. Initializing...");
            initLlm(modelFile.getAbsolutePath());
        } else {
            Log.d(TAG, "Model not found. Starting download.");
            LLMChat.setText("Downloading AI model (~1GB). Please wait...");
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
                        runOnUiThread(() -> LLMChat.setText("Download failed. Error Code: " + reason));
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
        runOnUiThread(() -> LLMChat.setText("Loading AI into memory..."));

        new Thread(() -> {
            try {
                // FIXED: Removed setResultListener from builder (it goes in generateResponseAsync)
                LlmInference.LlmInferenceOptions options = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(path)
                        .setMaxTokens(1024)
                        .build();

                llmInference = LlmInference.createFromOptions(this, options);
                runOnUiThread(() -> LLMChat.setText("AI Ready! Ask me anything about your footprint."));
                Log.d(TAG, "LlmInference created successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to init LLM", e);
                runOnUiThread(() -> LLMChat.setText("Init Error: " + e.getMessage()));
            }
        }).start();
    }

    private void generateResponse(String prompt) {
        if (llmInference == null) return;

        LLMChat.setText(ChatString + "\n\nThinking...");
        final boolean[] isFirstData = {true};

        llmInference.generateResponseAsync(prompt, (result, done) -> {
            runOnUiThread(() -> {
                String partialText = String.valueOf(result);

                if (isFirstData[0]) {
                    ChatString.append("\n\nAI Model:\n" + partialText);
                    isFirstData[0] = false;
                } else {
                    ChatString.append(partialText);
                }

                String formattedHtml = convertMarkdownToHtml(ChatString.toString());

                LLMChat.setText(HtmlCompat.fromHtml(
                        formattedHtml,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                ));
            });
        });
    }

    private String convertMarkdownToHtml(String text) {
        return text
                .replace("\\n", "\n")
                .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>")
                .replaceAll("\\*(.*?)\\*", "<i>$1</i>")
                .replace("\n", "<br>");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(onDownloadComplete); } catch (Exception ignored) {}
        if (llmInference != null) llmInference.close();
    }

    public void setUiColor() {
        if (FootprintStatus == 0) { // Good
            LLMChat.setTextColor(getResources().getColor(R.color.good_fg_color));
            ChatBox.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatBox.setTextColor(getResources().getColor(R.color.good_fg_color));
            ChatBox.setHintTextColor(getResources().getColor(R.color.good_fg_color));
            NavigationRow.setBackgroundColor(getResources().getColor(R.color.good_bg_color));
            //Set nav row item color
            HomeItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            HomeButton.setColorFilter(ContextCompat.getColor(this, R.color.good_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            HomeText.setTextColor(getResources().getColor(R.color.good_fg_color));
            ChatItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatButton.setColorFilter(ContextCompat.getColor(this, R.color.good_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            ChatText.setTextColor(getResources().getColor(R.color.good_fg_color));
            // Ui bg color (only for ui errors)
            main.setBackgroundColor(getResources().getColor(R.color.good_bg_color));
        } else if (FootprintStatus == 1) { // Bad
            LLMChat.setTextColor(getResources().getColor(R.color.bad_fg_color));
            ChatBox.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.bad_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatBox.setTextColor(getResources().getColor(R.color.bad_fg_color));
            ChatBox.setHintTextColor(getResources().getColor(R.color.bad_fg_color));
            NavigationRow.setBackgroundColor(getResources().getColor(R.color.bad_bg_color));
            //Set nav row item color
            HomeItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.bad_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            HomeButton.setColorFilter(ContextCompat.getColor(this, R.color.bad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            HomeText.setTextColor(getResources().getColor(R.color.bad_fg_color));
            ChatItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.bad_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatButton.setColorFilter(ContextCompat.getColor(this, R.color.bad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            ChatText.setTextColor(getResources().getColor(R.color.bad_fg_color));
            // Ui bg color (only for ui errors)
            main.setBackgroundColor(getResources().getColor(R.color.bad_bg_color));
        } else if (FootprintStatus == 2) { // Very Bad
            LLMChat.setTextColor(getResources().getColor(R.color.verybad_fg_color));
            ChatBox.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.verybad_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatBox.setTextColor(getResources().getColor(R.color.verybad_fg_color));
            ChatBox.setHintTextColor(getResources().getColor(R.color.verybad_fg_color));
            NavigationRow.setBackgroundColor(getResources().getColor(R.color.verybad_bg_color));
            //Set nav row item color
            HomeItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.verybad_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            HomeButton.setColorFilter(ContextCompat.getColor(this, R.color.verybad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            HomeText.setTextColor(getResources().getColor(R.color.verybad_fg_color));
            ChatItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.verybad_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatButton.setColorFilter(ContextCompat.getColor(this, R.color.verybad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            ChatText.setTextColor(getResources().getColor(R.color.verybad_fg_color));
            // Ui bg color (only for ui errors)
            main.setBackgroundColor(getResources().getColor(R.color.verybad_bg_color));
        }
    }
}