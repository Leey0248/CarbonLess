package com.homecoming.carbonless;

import android.media.Image;

public class FeedItem {
    private String ImageUrl;
    private String Title;
    private String Summary;

    public FeedItem(String image, String title, String summary) {
        this.ImageUrl = image;
        this.Title = title;
        this.Summary = summary;
    }

    public String getImageUrl(){
        return ImageUrl;
    }
    public String getTitle() {
        return Title;
    }
    public String getSummary() {
        return Summary;
    }
}