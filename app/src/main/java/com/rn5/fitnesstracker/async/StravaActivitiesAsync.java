package com.rn5.fitnesstracker.async;

import android.os.AsyncTask;
import android.util.Log;

import com.rn5.fitnesstracker.define.EventListener;
import com.rn5.fitnesstracker.model.AthleteDetail;
import com.rn5.fitnesstracker.model.StravaActivity;
import com.rn5.libstrava.activities.api.ActivityAPI;
import com.rn5.libstrava.activities.model.Activity;
import com.rn5.libstrava.common.api.StravaConfig;
import com.rn5.libstrava.exception.StravaAPIException;
import com.rn5.libstrava.stream.api.StreamAPI;
import com.rn5.libstrava.stream.model.Stream;

import static com.rn5.fitnesstracker.activity.MainActivity.activities;
import static com.rn5.fitnesstracker.activity.MainActivity.athleteData;
import static com.rn5.fitnesstracker.activity.MainActivity.dayInMS;
import static com.rn5.fitnesstracker.activity.MainActivity.getDaysTo;
import static com.rn5.fitnesstracker.define.Constants.sdf;
import static com.rn5.fitnesstracker.define.Constants.sdfDate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StravaActivitiesAsync extends AsyncTask<String,Void, List<Activity>> {

    private static final String TAG = StravaActivitiesAsync.class.getSimpleName();
    private EventListener eventListener;

    public StravaActivitiesAsync(EventListener eventListener) {
        this.eventListener = eventListener;
        Log.d(TAG,"Created.");
    }

    @Override
    protected List<Activity> doInBackground(String... strings) {
        Log.d(TAG,"Last update [" + athleteData.getLastUpdateTime() + "]");
        StravaConfig config = StravaConfig.auth()
                .debug()
                .build();
        ActivityAPI api = new ActivityAPI(config);
        Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        List<Activity> activities = null;
        try {
            if (athleteData.getLastUpdateTime() == null) {
                Calendar dt = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
                dt.add(Calendar.DATE,-180);
                athleteData.setLastUpdateTime(dt.getTimeInMillis());
            }
            activities = api.getActivities(athleteData.getLastUpdateTime() / 1000, gmtDate.getTimeInMillis() / 1000).execute();
        } catch (StravaAPIException e) {
            Log.e(TAG,"StravaAPIException[" + e.getMessage() + "]");
        }
        Log.d(TAG,"api.getActivities()");

        List<Double> pow30 = new ArrayList<>();
        if (activities != null) {
            for (Activity activity : activities) {
                StreamAPI streamAPI = new StreamAPI(config);
                Stream stream = streamAPI.getStreams(activity.getId()).execute();

                Date date;
                try {
                    date = sdf.parse(activity.getStartDate());
                } catch (ParseException e) {
                    date = new Date();
                }

                Calendar activityDate = Calendar.getInstance();
                activityDate.setTime(date);

                AthleteDetail athleteDetail = new AthleteDetail(245,182,65,getDaysTo(Calendar.getInstance()));
                for (AthleteDetail detail : athleteData.getDetailList()) {
                    if (detail.getDate() > getDaysTo(activityDate))
                        break;
                    athleteDetail = detail;
                }

                double ftp = (double) athleteDetail.getFtp();
                double hrm = (double) athleteDetail.getHrm();
                double hrr = (double) athleteDetail.getHrr();

                Calendar c = Calendar.getInstance();
                long dtMillis = athleteDetail.getDate() * dayInMS - c.getTimeZone().getRawOffset();
                c.setTimeInMillis(dtMillis);
                Log.d(TAG,"Activity Date[" + sdfDate.format(date.getTime()) + "] DATE[" + sdfDate.format(c.getTime()) +
                        "] FTP[" + ftp + "] HRM[" + hrm + "] HRR[" + hrr + "]");

                //Double pst = 0d;
                double rollAvgPow = 0d;
                double st = (double) activity.getMovingTime();
                double psc = 0d;
                /*
                if (stream.getDistance() != null)
                    st = (double) stream.getDistance().getOriginalSize();
                 */
                int i = 0;
                if (stream.getWatts() != null) {
                    for (Double val : stream.getWatts().getData()) {
                        if (stream.getMoving().getData().get(i)) {
                            if (val != null) {
                                pow30.add(0,val);
                                if (pow30.size() > 30)
                                    pow30.remove(30);
                                psc ++;
                                rollAvgPow += Math.pow(getAvg30(pow30),4);
                            }
                        }
                        i++;
                    }
                }
                double wp = Math.pow((rollAvgPow/psc),0.25d);
                double intensity = wp/ftp;
                Log.d(TAG,"Weighted Power [" + wp + "] Intensity [" + intensity + "]");
                double pss = wp*(wp/ftp)*st/(ftp*3600d)*100d;
                Log.d(TAG,"StartDateLocal:" + date);
                int iPSS = (int) Math.round(pss);

                i = 0;
                double hrsc = 0d;
                double hrR;
                double trimp = 0d;
                double hrtot = 0d;
                double lastT = 0d;
                double thisT;
                if (stream.getHeartrate() != null) {
                    for (Double val : stream.getHeartrate().getData()) {
                        thisT = stream.getTime().getData().get(i);
                        if (val == null) {
                            val = 0d;
                        }
                        hrR = (val-hrr)/(hrm-hrr);
                        trimp += (thisT-lastT)/60d*hrR*0.64d*Math.exp(1.92d*hrR);
                        lastT = thisT;
                        i++;
                    }
                }
                double hravg = hrtot/hrsc;
                //hrr = (hravg-hrr)/(hrm-hrr);
                //trimp = (st/60d)*hrr*0.64d*Math.exp(1.92d*hrr);
                double hrss = (int) (trimp/getHRatLTHR()*100d);
                int iHRSS = (int) Math.round(hrss);
                Log.d(TAG,"TRIMP [" + trimp + "] HRSS [" + hrss + "] HRAvg[" + hravg + "]");
                StravaActivity stravaActivity = new StravaActivity(activity.getId(), (iPSS>0?iPSS:iHRSS), iHRSS, date.getTime());
                athleteData.getActivityList().put(stravaActivity.getId(),stravaActivity);
                Log.e("PSS","PSS[" + pss + "]");
            }
        }
        athleteData.save();

        return activities;
    }

    @Override
    protected void onPostExecute(List<Activity> result) {
        Log.d(TAG,"onPostExecute()");
        activities.setActivities(result);
        eventListener.onActivitySynced();
    }

    private double getAvg30(List<Double> inList) {
        double val = 0d;
        for (Double inVal : inList) {
            val += inVal;
        }
        return val/30d;
    }

    private double getHRatLTHR() {
        double hrR = ((182d*0.90d)-65)/(182-65);
        return 60d*hrR*0.64d*Math.exp(1.92d*hrR);
    }
}
