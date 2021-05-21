package com.rn5.fitnesstracker.model;

import android.util.Log;
import android.util.LongSparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rn5.fitnesstracker.define.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

import static com.rn5.fitnesstracker.activity.MainActivity.fitnessArray;
import static com.rn5.fitnesstracker.activity.MainActivity.getDaysTo;
import static com.rn5.fitnesstracker.define.Constants.APP_FILE_PATH;
import static com.rn5.fitnesstracker.define.Constants.getObjectFromJsonString;
import static com.rn5.fitnesstracker.define.Constants.loadFile;
import static com.rn5.fitnesstracker.define.Constants.saveFile;
import static java.util.Arrays.asList;

@lombok.Data
public class AthleteData {
    private static final String TAG = AthleteData.class.getSimpleName();

    private Long lastUpdateTime;
    private Integer age;
    private List<AthleteDetail> detailList = new ArrayList<>();
    private List<StravaActivity> activityList = new ArrayList<>();
    private List<Fitness> fitnessList = new ArrayList<>();
    private List<Ftp> ftpList = new ArrayList<>();

    private static final String FILE_NAME = "AthleteData.json";

    public AthleteData() {}

    public void addAthleteDetail(AthleteDetail athleteDetail) {
        int i = 0;
        if (detailList.size() > 0) {
            for (AthleteDetail detail : detailList) {
                if (detail.getDate() < athleteDetail.getDate()) {
                    break;
                }
                i++;
            }
        }
        this.detailList.add(i, athleteDetail);
    }

    public void updateAthleteDetail(AthleteDetail athleteDetail) {
        int i = 0;
        for (AthleteDetail d : detailList) {
            if (d.getId() == athleteDetail.getId()) {
                break;
            }
            i++;
        }
        detailList.set(i, athleteDetail);
    }

    public void removeAthleteDetail(AthleteDetail athleteDetail) {
        int i = 0;
        for (AthleteDetail d : detailList) {
            if (d == athleteDetail) {
                detailList.remove(i);
                break;
            }
            i++;
        }
    }

    public void updateFitnessList() {
        List<Fitness> list = new ArrayList<>();
        for(int i = 0; i < fitnessArray.size(); i++) {
            long key = fitnessArray.keyAt(i);
            Fitness obj = fitnessArray.get(key);
            list.add(obj);
        }
        this.fitnessList = list;
    }

    public void save() {
        try {
            Gson gson = new GsonBuilder().create();
            String val = gson.toJson(this);
            saveFile(APP_FILE_PATH, FILE_NAME, val);
        } catch (IOException e) {
            Log.e("AthleteData","SAVE FAILED : " + e.getMessage());
        }
    }

    public static AthleteData loadFromFile() {
        try {
            String val = loadFile(APP_FILE_PATH, FILE_NAME);
            return getObjectFromJsonString(val, AthleteData.class);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "loadFromFile() FileNotFoundException");
            AthleteData data = new AthleteData();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -1);
            data.getDetailList().add(new AthleteDetail(100, 182, 65, getDaysTo(c), getDaysTo(c)));
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Getter
    @Setter
    public static class Ftp {
        int ftp;
        int hr;
        long date;

        public Ftp() {}
        public Ftp withFtp(int ftp) {
            this.ftp = ftp;
            return this;
        }
        public Ftp withHr(int hr) {
            this.hr = hr;
            return this;
        }
        public Ftp withDate(long dt) {
            this.date = dt;
            return this;
        }

    }

}
