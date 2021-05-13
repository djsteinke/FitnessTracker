package com.rn5.fitnesstracker.define;

import android.icu.text.SimpleDateFormat;

import com.rn5.fitnesstracker.model.StravaToken;

import java.io.File;
import java.util.Locale;

public class Constants {
    public static File APP_FILE_PATH;

    public static StravaToken STRAVA_TOKEN;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    public static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
}
