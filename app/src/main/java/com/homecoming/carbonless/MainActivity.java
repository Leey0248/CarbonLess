package com.homecoming.carbonless;

import android.annotation.SuppressLint;
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

public class MainActivity extends AppCompatActivity {

    int FootprintStatus = 0; //0= good, 1= bad, 2= very bad
    ImageView BackgroundImage;
    ImageView FeedBackground;
    LinearLayout NavigationRow;
    LinearLayout HomeItem;
    ImageView HomeButton;
    TextView HomeText;
    ConstraintLayout main;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BackgroundImage = findViewById(R.id.BackgroundImage);
        FeedBackground = findViewById(R.id.FeedBackground);
        NavigationRow = findViewById(R.id.NavigationRow);
        HomeItem = findViewById(R.id.HomeItem);
        HomeButton = findViewById(R.id.HomeButton);
        HomeText = findViewById(R.id.HomeText);
        main = findViewById(R.id.main);
        if (FootprintStatus == 0) { // Good
            BackgroundImage.setImageResource(R.drawable.good_bg);
            FeedBackground.setColorFilter(ContextCompat.getColor(this, R.color.light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            NavigationRow.setBackgroundColor(getResources().getColor(R.color.light_blue));
            //Set nav row item color
            HomeItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            HomeButton.setColorFilter(ContextCompat.getColor(this, R.color.good_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            HomeText.setTextColor(getResources().getColor(R.color.good_fg_color));
            // Ui bg color (only for ui errors)
            main.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }
    }
}