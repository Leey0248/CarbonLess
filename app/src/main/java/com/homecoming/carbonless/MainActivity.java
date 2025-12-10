package com.homecoming.carbonless;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    int FootprintStatus = 0; //0= good, 1= bad, 2= very bad
    ImageView BackgroundImage;
    ImageView FeedBackground;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BackgroundImage = findViewById(R.id.BackgroundImage);
        FeedBackground = findViewById(R.id.FeedBackground);
        if (FootprintStatus == 0) {
            BackgroundImage.setImageResource(R.drawable.good_bg);
            FeedBackground.setColorFilter(ContextCompat.getColor(this, R.color.light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}