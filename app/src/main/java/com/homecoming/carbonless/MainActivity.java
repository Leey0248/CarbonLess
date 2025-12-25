package com.homecoming.carbonless;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int FootprintStatus = 0; // 0 = good, 1 = bad, 2 = very bad
    double CarbonFootprintDaily = 2;
    String UserName = "George";
    ImageView BackgroundImage;
    ImageView FeedBackground;
    ImageView CreateFeed;
    LinearLayout NavigationRow;
    LinearLayout HomeItem;
    ImageView HomeButton;
    TextView HomeText;
    LinearLayout ChatItem;
    ImageView ChatButton;
    TextView ChatText;
    View CarbnFootprintTouchArea;
    ConstraintLayout main;
    RecyclerView recyclerView;
    List<FeedItem> FeedList = new ArrayList<>();
    FeedItemAdapter adapter;
    TextView title;
    TextView CarbnFootprint;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Force the window to resize when the keyboard appears
        setContentView(R.layout.activity_main);
        BackgroundImage = findViewById(R.id.BackgroundImage);
        FeedBackground = findViewById(R.id.FeedBackground);
        CreateFeed = findViewById(R.id.CreateFeed);
        NavigationRow = findViewById(R.id.NavigationRow);
        HomeItem = findViewById(R.id.HomeItem);
        HomeButton = findViewById(R.id.HomeButton);
        HomeText = findViewById(R.id.HomeText);
        ChatItem = findViewById(R.id.ChatItem);
        ChatButton = findViewById(R.id.ChatButton);
        ChatText = findViewById(R.id.ChatText);
        CarbnFootprintTouchArea = findViewById(R.id.CarbnFootprintTouchArea);
        CarbnFootprintTouchArea.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CarbonFootprintGeneral.class)));

        ChatItem.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AIChat.class);
            startActivity(intent);
        });
        main = findViewById(R.id.main);
        CarbnFootprint = findViewById(R.id.CarbnFootprint);
        CarbnFootprint.setText(CarbonFootprintDaily + " kg CO2e");
        // Normal (Sustainable): 6 – 7 kg	(This is the target "Earth-friendly" daily budget for 2030.)
        // Global Average: 12 – 18 kg	(The current actual average per person globally.)
        // High: 35 – 50 kg	(Common in Western Europe or for frequent travelers.)
        // Too High: Over 60 kg	(The average for residents of the US, Canada, or Australia.)
        if (CarbonFootprintDaily > 16 && CarbonFootprintDaily <= 30) {
            FootprintStatus = 1;
        } else if (CarbonFootprintDaily > 30) {
            FootprintStatus = 2;
        }
        setUiColor();

        title = findViewById(R.id.title);
        if (UserName != null) {
            title.setText("Hi, " + UserName + "!");
        }

        recyclerView = findViewById(R.id.FeedView);
        loadFeed();
    }

    public void loadFeed() {
        // Setting up the RecyclerView
        FeedList.clear();
        recyclerView = findViewById(R.id.FeedView);

        // Insert data
        FeedList.add(new FeedItem("https://cdn.iview.abc.net.au/thumbs/1152/zw/ZW3739A038S00_67205bc59312f.jpg", "Item 1", "The quick brown fox jumps over the box."));
        FeedList.add(new FeedItem("https://cdn.iview.abc.net.au/thumbs/1152/zw/ZW2487A035S00_6242660dd04c0.jpg", "Item 2", "Peppa helps Danny Dog with a bedroom make-over on the theme of pirates and sea monsters."));
        FeedList.add(new FeedItem("https://cdn.iview.abc.net.au/thumbs/1152/zw/ZW2487A031S00_6239303f2e23e.jpg", "Item 3", "Peppa and her friends are playing in their clubhouse. The Club House has a fold down counter at the front. The children pretend to be a little shop for the parents to buy things from."));

        adapter = new FeedItemAdapter(FeedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    public void setUiColor() {
        if (FootprintStatus == 0) { // Good
            BackgroundImage.setImageResource(R.drawable.good_bg);
            FeedBackground.setColorFilter(ContextCompat.getColor(this, R.color.good_bg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            NavigationRow.setBackgroundColor(getResources().getColor(R.color.good_bg_color));
            //Set create feed bg color
            CreateFeed.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_bg_color), PorterDuff.Mode.SRC_ATOP);
            //Set nav row item color
            HomeItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            HomeButton.setColorFilter(ContextCompat.getColor(this, R.color.good_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            HomeText.setTextColor(getResources().getColor(R.color.good_fg_color));
            ChatItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.good_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatButton.setColorFilter(ContextCompat.getColor(this, R.color.good_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            ChatText.setTextColor(getResources().getColor(R.color.good_fg_color));
            // Ui bg color (only for ui errors)
            main.setBackgroundColor(getResources().getColor(R.color.good_bg_color));
        } else if (FootprintStatus == 1) { // Bad
            BackgroundImage.setImageResource(R.drawable.bad_bg);
            FeedBackground.setColorFilter(ContextCompat.getColor(this, R.color.bad_bg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            NavigationRow.setBackgroundColor(getResources().getColor(R.color.bad_bg_color));
            //Set create feed bg color
            CreateFeed.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.bad_bg_color), PorterDuff.Mode.SRC_ATOP);
            //Set nav row item color
            HomeItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.bad_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            HomeButton.setColorFilter(ContextCompat.getColor(this, R.color.bad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            HomeText.setTextColor(getResources().getColor(R.color.bad_fg_color));
            ChatItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.bad_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatButton.setColorFilter(ContextCompat.getColor(this, R.color.bad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            ChatText.setTextColor(getResources().getColor(R.color.bad_fg_color));
            // Ui bg color (only for ui errors)
            main.setBackgroundColor(getResources().getColor(R.color.bad_bg_color));
        } else if (FootprintStatus == 2) { // Very Bad
            BackgroundImage.setImageResource(R.drawable.verybad_bg);
            FeedBackground.setColorFilter(ContextCompat.getColor(this, R.color.verybad_bg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            NavigationRow.setBackgroundColor(getResources().getColor(R.color.verybad_bg_color));
            //Set create feed bg color
            CreateFeed.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.verybad_bg_color), PorterDuff.Mode.SRC_ATOP);
            //Set nav row item color
            HomeItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.verybad_item_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            HomeButton.setColorFilter(ContextCompat.getColor(this, R.color.verybad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            HomeText.setTextColor(getResources().getColor(R.color.verybad_fg_color));
            ChatItem.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.verybad_bg_color), android.graphics.PorterDuff.Mode.SRC_ATOP);
            ChatButton.setColorFilter(ContextCompat.getColor(this, R.color.verybad_fg_color), android.graphics.PorterDuff.Mode.SRC_IN);
            ChatText.setTextColor(getResources().getColor(R.color.verybad_fg_color));
            // Ui bg color (only for ui errors)
            main.setBackgroundColor(getResources().getColor(R.color.verybad_bg_color));
        }
    }
}