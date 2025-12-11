package com.homecoming.carbonless;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AIChat extends AppCompatActivity {

    int FootprintStatus = 0; //0 = good, 1 = bad, 2 = very bad
    LinearLayout NavigationRow;
    LinearLayout HomeItem;
    ImageView HomeButton;
    TextView HomeText;
    LinearLayout ChatItem;
    ImageView ChatButton;
    TextView ChatText;
    ConstraintLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aichat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        NavigationRow = findViewById(R.id.NavigationRow);
        HomeItem = findViewById(R.id.HomeItem);
        HomeButton = findViewById(R.id.HomeButton);
        HomeText = findViewById(R.id.HomeText);
        HomeItem.setOnClickListener(v -> {
            Intent intent = new Intent(AIChat.this, MainActivity.class);
            startActivity(intent);
        });
        ChatItem = findViewById(R.id.ChatItem);
        ChatButton = findViewById(R.id.ChatButton);
        ChatText = findViewById(R.id.ChatText);
        main = findViewById(R.id.main);
        if (FootprintStatus == 0) { // Good
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