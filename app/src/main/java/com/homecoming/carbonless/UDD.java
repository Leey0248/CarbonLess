package com.homecoming.carbonless;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

public class UDD { // Unified Data Directory
    static double CarbonFootprintDaily = 24; // in kg
    static double CarbonFootprint = 0.6; // in t
    static String Username = "Peter";
    static boolean DataLoaded = false; // Must be defined at class level

    public static void StartLoadingData(Context context) {
        if (context == null) return;

        Log.d("DataLoaded", String.valueOf(DataLoaded));
        if (!DataLoaded) {
            new CountDownTimer(400, 100) {

                public void onTick(long millisUntilFinished) {}

                public void onFinish() {
                    SendBroadcast(context);
                    DataLoaded = true;
                }
            }.start();
        } else {
            SendBroadcast(context);
        }
    }
    private static void SendBroadcast(Context context) {
        Intent intent = new Intent("com.homecoming.carbonless.onDataLoaded");
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
    public static String GetUsername() {
        return Username;
    }

    public static double GetDailyFootprint() {
        return CarbonFootprintDaily;
    }

    public static int GetDailyFootprintStatus() {
        // Normal (Sustainable): 6 – 7 kg (This is the target "Earth-friendly" daily budget for 2030.)
        // Global Average: 12 – 18 kg (The current actual average per person globally.)
        // High: 35 – 50 kg	(Common in Western Europe or for frequent travelers.)
        // Too High: Over 60 kg	(The average for residents of the US, Canada, or Australia.)
        if (CarbonFootprintDaily <= 16) {
            return 0;
        } else if (CarbonFootprintDaily > 16 && CarbonFootprintDaily <= 30) {
            return 1;
        } else if (CarbonFootprintDaily > 30) {
            return 2;
        }

        Log.d("UDD_Log", "Error in GetDailyFootprintStatus(), returning 3.");
        return 3;
    }

    public static double GetFootprint() {
        return CarbonFootprint;
    }

    public static int GetFootprintStatus() {
        // Global Average: 0.4 – 0.5 t (Based on the global average of ~4.8 to 6.0 tonnes per year.)
        // Normal (City): 0.7 – 1.0 t	(Typical for an urban resident with moderate energy use and public transport habits.)
        // High: 1.2 – 2.0 t (Common for frequent flyers, car owners, or those with high meat consumption.)
        // Too High: > 2.5 t (Excessive levels often linked to luxury lifestyles or heavy international travel.)
        if (CarbonFootprint <= 1) {
            return 0;
        } else if (CarbonFootprint > 1 && CarbonFootprint <= 2.35) {
            return 1;
        } else if (CarbonFootprint > 2.35) {
            return 2;
        }

        Log.d("UDD_Log", "Error in GetFootprintStatus(), returning 3.");
        return 3;
    }
}
