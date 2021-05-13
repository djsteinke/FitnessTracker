package com.rn5.fitnesstracker.model;

import android.util.Log;
import android.util.LongSparseArray;

import com.rn5.fitnesstracker.define.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.rn5.fitnesstracker.define.Constants.APP_FILE_PATH;

@lombok.Data
public class AthleteData {
    private static final String TAG = AthleteData.class.getSimpleName();

    private Long lastUpdateTime;
    private Integer age;
    private List<AthleteDetail> detailList = new ArrayList<>();
    private LongSparseArray<StravaActivity> activityList = new LongSparseArray<>();
    private LongSparseArray<Fitness> fitnessList = new LongSparseArray<>();

    private static final String FILE_NAME = "AthleteData.json";

    public AthleteData() {
        try {
            JSONObject object = Json.loadJSONFromFile(APP_FILE_PATH,FILE_NAME);
            if (object != null) {
                this.age = Json.getJSONInt(object, "age", null);

                Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
                gmtDate.add(Calendar.DATE,-30);
                this.lastUpdateTime = Json.getJSONLong(object, "last_update_time", gmtDate.getTimeInMillis());
                JSONArray ftpArray = object.getJSONArray("athleteDetails");
                for (int i=0;i<ftpArray.length();i++) {
                    addAthleteDetail(new AthleteDetail(ftpArray.getJSONObject(i)));
                }
                JSONArray activityArray = object.getJSONArray("activityArray");
                for (int i=0;i<activityArray.length();i++) {
                    StravaActivity stravaActivity = new StravaActivity(activityArray.getJSONObject(i));
                    //Log.d(TAG,"StravaActivity[" + stravaActivity.getId() + "]");
                    activityList.put(stravaActivity.getId(),stravaActivity);
                }
                JSONArray fitnessArray = object.getJSONArray("fitnessArray");
                for (int i=0;i<fitnessArray.length();i++) {
                    Fitness fitness = new Fitness(fitnessArray.getJSONObject(i));
                    //Log.d(TAG,"Fitness[" + fitness.getId() + "]");
                    fitnessList.put(fitness.getId(),fitness);
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"CREATE FAILED : " + e.getMessage());
        }
    }

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

    public void removeAthleteDetail(int pos) {
        detailList.remove(pos);
    }

    public void save() {
        try {
            JSONObject object = this.toJson();
            Json.saveJSONToFile(APP_FILE_PATH, FILE_NAME, object);
        } catch (IOException | JSONException e) {
            Log.e("AthleteData","SAVE FAILED : " + e.getMessage());
        }
    }

    private JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("age",age);
        object.put("last_update_time",lastUpdateTime);

        JSONArray detailsArray = new JSONArray();
        for (AthleteDetail athleteDetail : detailList) {
            detailsArray.put(athleteDetail.toJson());
        }
        object.put("athleteDetails",detailsArray);

        JSONArray activityArray = new JSONArray();

        for (int i=0;i<activityList.size();i++) {
            long key = activityList.keyAt(i);
            StravaActivity activity = activityList.get(key);
            activityArray.put(activity.toJson());
        }
        object.put("activityArray",activityArray);

        JSONArray fitnessArray = new JSONArray();
        for (int i=0;i<fitnessList.size();i++) {
            long key = fitnessList.keyAt(i);
            Fitness fitness = fitnessList.get(key);
            fitness.setId(key);
            fitnessArray.put(fitness.toJson());
        }
        object.put("fitnessArray",fitnessArray);

        return object;
    }
}
