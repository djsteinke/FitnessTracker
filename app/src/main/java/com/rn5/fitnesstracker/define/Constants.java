package com.rn5.fitnesstracker.define;

import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rn5.fitnesstracker.model.AthleteDetail;
import com.rn5.fitnesstracker.model.StravaToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Constants {
    public static File APP_FILE_PATH;

    public static StravaToken STRAVA_TOKEN;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    public static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static <C> List<C> asList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> list = new ArrayList<>();
        for(int i = 0; i < sparseArray.size(); i++) {
            long key = sparseArray.keyAt(i);
            C obj = sparseArray.get(key);
            list.add(obj);
        }
        return list;
    }

    public static <T> List<T> updateList(List<T> list, T t) {
        int i = 0;
        boolean found = false;
        for (T d : list) {
            if (d == t) {
                found = true;
                break;
            }
            i++;
        }
        if (found)
            list.set(i, t);
        else
            list.add(t);
        return list;
    }

    public static void saveFile(File dir, String fileName, String val) throws IOException {
        File file = new File(dir,fileName);
        if (file.exists() || (!file.exists() && file.createNewFile())) {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(val);
            bw.close();
            fw.close();
        }
        Log.d("saveFile","File[" + fileName + "] saved.");
    }

    public static String loadFile(File dir, String fileName) throws Exception {
        File file = new File(dir,fileName);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bfr.readLine()) != null) {
                sb.append(line).append("\n");
            }
            bfr.close();
            fis.close();
            Log.d("loadJSONFromFile","File[" + fileName + "] loaded.");
            return sb.toString();
        } else {
            Log.d("loadJSONFromFile","File[" + fileName + "] does not exist.");
            throw new FileNotFoundException();
        }
    }

    public static <T> T getObjectFromJsonString(String val, Class<T> t) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(val, t);
    }
}
