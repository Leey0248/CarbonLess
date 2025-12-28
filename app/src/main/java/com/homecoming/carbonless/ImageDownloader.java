package com.homecoming.carbonless;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownloader {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void downloadAndSetImage(Context context, String imageUrl, ImageView imageView) {
        executorService.execute(() -> {
            Bitmap bitmap = null;

            // CHECK: Is this a local resource?
            if (imageUrl.startsWith("file:///android_res/")) {
                // Extract the resource name (e.g., "art1")
                String resName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());

                if (resId != 0) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                }
            } else {
                // PROCEED WITH EXISTING WEB DOWNLOAD LOGIC
                String filename = String.valueOf(imageUrl.hashCode());
                File cacheFile = new File(context.getCacheDir(), filename);
                try {
                    // 1. Download the image data from the URL
                    InputStream inputStream = new URL(imageUrl).openStream();

                    // 2. Decode the stream into a Bitmap object
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the error (e.g., set an error placeholder)
                }
            }

            // Update UI logic remains the same
            final Bitmap finalBitmap = bitmap;
            mainHandler.post(() -> {
                if (finalBitmap != null) {
                    imageView.setImageBitmap(finalBitmap);
                } else {
                    imageView.setImageResource(R.drawable.generic_image);
                }
            });
        });
    }
    private static Bitmap downloadImage(String urlString) {
        try (InputStream inputStream = new URL(urlString).openStream()) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveToDisk(File file, Bitmap bitmap) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}