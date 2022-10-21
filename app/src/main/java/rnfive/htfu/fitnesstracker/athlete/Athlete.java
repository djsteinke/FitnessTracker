package rnfive.htfu.fitnesstracker.athlete;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rnfive.htfu.fitnesstracker.athlete.detail.AthleteDetail;
import rnfive.htfu.fitnesstracker.athlete.fitness.Ftp;
import rnfive.htfu.fitnesstracker.athlete.fitness.Fitness;
import rnfive.htfu.fitnesstracker.strava.StravaActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import rnfive.htfu.fitnesstracker.MainActivity;
import rnfive.htfu.fitnesstracker.util.Constants;

@Getter
@Setter
public class Athlete {
    private static final String TAG = Athlete.class.getSimpleName();

    private Long lastUpdateTime;
    private Integer age;
    private List<AthleteDetail> detailList = new ArrayList<>();
    private List<StravaActivity> activityList = new ArrayList<>();
    private List<Fitness> fitnessList = new ArrayList<>();
    private List<Ftp> ftpList = new ArrayList<>();

    private static final String FILE_NAME = "AthleteData.json";

    public Athlete() {}

    public void addAthleteDetail(AthleteDetail athleteDetail) {
        int i = 0;
        if (detailList.size() > 0) {
            for (AthleteDetail detail : detailList) {
                if (detail.getId() < athleteDetail.getId()) {
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
            if (d.equals(athleteDetail)) {
                break;
            }
            i++;
        }
        detailList.set(i, athleteDetail);
    }

    public void removeAthleteDetail(AthleteDetail athleteDetail) {
        int i = 0;
        for (AthleteDetail d : detailList) {
            if (d.equals(athleteDetail)) {
                detailList.remove(i);
                break;
            }
            i++;
        }
    }

    public void updateFitnessList() {
        List<Fitness> list = new ArrayList<>();
        for(int i = 0; i < MainActivity.fitnessArray.size(); i++) {
            long key = MainActivity.fitnessArray.keyAt(i);
            Fitness obj = MainActivity.fitnessArray.get(key);
            list.add(obj);
        }
        this.fitnessList = list;
    }

    public void save() {

        detailList.sort(Comparator.reverseOrder());
        try {
            Gson gson = new GsonBuilder().create();
            String val = gson.toJson(this);
            Constants.saveFile(Constants.APP_FILE_PATH, FILE_NAME, val);
        } catch (IOException e) {
            Log.e("AthleteData","SAVE FAILED : " + e.getMessage());
        }
    }

    public static Athlete loadFromFile() {
        try {
            String val = Constants.loadFile(Constants.APP_FILE_PATH, FILE_NAME);
            return Constants.getObjectFromJsonString(val, Athlete.class);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "loadFromFile() FileNotFoundException");
            Athlete data = new Athlete();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -1);
            data.getDetailList().add(new AthleteDetail(100, 182, 65, MainActivity.getDaysTo(c)));
            return data;
        } catch (Exception e) {
            return null;
        }
    }

}
