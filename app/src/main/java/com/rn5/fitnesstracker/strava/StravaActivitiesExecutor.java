package com.rn5.fitnesstracker.strava;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.rn5.fitnesstracker.util.EventListener;
import com.rn5.fitnesstracker.athlete.fitness.Ftp;
import com.rn5.fitnesstracker.athlete.detail.AthleteDetail;
import com.rn5.libstrava.activities.api.ActivityAPI;
import com.rn5.libstrava.activities.model.Activity;
import com.rn5.libstrava.common.api.StravaConfig;
import com.rn5.libstrava.exception.StravaAPIException;
import com.rn5.libstrava.stream.api.StreamAPI;
import com.rn5.libstrava.stream.model.Stream;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.rn5.fitnesstracker.MainActivity.athlete;
import static com.rn5.fitnesstracker.MainActivity.dayInMS;
import static com.rn5.fitnesstracker.MainActivity.getDaysTo;
import static com.rn5.fitnesstracker.util.Constants.sdf;
import static com.rn5.fitnesstracker.util.Constants.sdfDate;
import static com.rn5.fitnesstracker.util.Constants.updateList;

public class StravaActivitiesExecutor {

    private static final String TAG = StravaActivitiesExecutor.class.getSimpleName();
    private final EventListener eventListener;
    private int ftpS = 0;
    private int ftpL = 0;
    private int hrMax = 0;
    private static final int ftpDays = 90;
    private static final int hrDays = 180;
    private final List<Integer> ftpAS = new ArrayList<>();
    private final List<Integer> ftpAL = new ArrayList<>();
    private AthleteDetail athleteDetail;
    private Calendar activityDate;

    public StravaActivitiesExecutor(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void run() {
        Handler handler = new Handler(Looper.getMainLooper());
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Log.d(TAG,"Last update [" + athlete.getLastUpdateTime() + "]");
            StravaConfig config = StravaConfig.auth()
                    .debug()
                    .build();
            ActivityAPI api = new ActivityAPI(config);
            Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            List<Activity> activities = null;
            try {
                if (athlete.getLastUpdateTime() == null) {
                    Calendar dt = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
                    dt.add(Calendar.DATE,-180);
                    athlete.setLastUpdateTime(dt.getTimeInMillis());
                }
                activities = api.getActivities(athlete.getLastUpdateTime() / 1000, gmtDate.getTimeInMillis() / 1000).execute();
            } catch (StravaAPIException e) {
                Log.e(TAG,"StravaAPIException[" + e.getMessage() + "]");
            }
            Log.d(TAG,"api.getActivities()");

            List<Double> pow30 = new ArrayList<>();
            if (activities != null) {
                Collections.sort(activities);
                for (Activity activity : activities) {
                    ftpAS.clear();
                    ftpAL.clear();
                    ftpS = 0;
                    ftpL = 0;
                    StreamAPI streamAPI = new StreamAPI(config);
                    Stream stream = streamAPI.getStreams(activity.getId()).execute();

                    Date date;
                    try {
                        date = sdf.parse(activity.getStartDate());
                    } catch (ParseException e) {
                        date = new Date();
                    }

                    activityDate = Calendar.getInstance();
                    activityDate.setTime(date);
                    Log.d(TAG, "getDaysTo: " + getDaysTo(activityDate));
                    setCurrentDetail();
                    Log.d(TAG, athleteDetail.toString());


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
                    double powTot = 0.0d;
                    double st = (double) activity.getMovingTime();
                    double psc = 0d;

                    int i = 0;
                    if (stream.getWatts() != null) {
                        for (Double val : stream.getWatts().getData()) {
                            if (stream.getMoving().getData().get(i)) {
                                if (val != null) {
                                    setFtp((int)Math.round(val));
                                    pow30.add(0,val);
                                    if (pow30.size() > 30)
                                        pow30.remove(30);
                                    psc ++;
                                    powTot += val;
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
                    double powAvg = (psc > 0 ? powTot/psc : 0.0d);

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
                            hrm = Math.max(hrm, val);
                            hrMax = (int)Math.max(hrMax, val);
                            hrtot += val;
                            hrsc ++;
                            hrR = (val-hrr)/(hrm-hrr);
                            trimp += (thisT-lastT)/60d*hrR*0.64d*Math.exp(1.92d*hrR);
                            lastT = thisT;
                            i++;
                        }
                    }
                    double hravg = (hrsc > 0 ? hrtot/hrsc : 0.0d);
                    //hrr = (hravg-hrr)/(hrm-hrr);
                    //trimp = (st/60d)*hrr*0.64d*Math.exp(1.92d*hrr);
                    double hrss = (int) (trimp/getHRatLTHR()*100d);
                    int iHRSS = (int) Math.round(hrss);
                    Log.d(TAG,"TRIMP [" + trimp + "] HRSS [" + hrss + "] HRAvg[" + hravg + "]");

                    double distance = 0.0d;
                    if (stream.getDistance() != null) {
                        for (Double val : stream.getDistance().getData()) {
                            if (val == null) {
                                val = 0d;
                            }
                            distance = Math.max(distance, val);
                        }
                    }

                    StravaActivity stravaActivity = new StravaActivity(activity.getId())
                            .withFtpEffort(iPSS)
                            .withHrEffort(iHRSS)
                            .withDate(date.getTime());
                    stravaActivity.setAvg20Pwr(ftpS);
                    stravaActivity.setDistance(distance);
                    stravaActivity.setMovingTime(activity.getMovingTime());
                    stravaActivity.setAvgHr((int) hravg);
                    stravaActivity.setAvgPwr((int) powAvg);
                    stravaActivity.setActivityType(activity.getType());
                    updateList(athlete.getActivityList(), stravaActivity);
                    Log.e("PSS","PSS[" + pss + "]");
                    if (ftpS > 0 || ftpL > 0) {
                        athlete.getFtpList().add(new Ftp()
                                .withFtp(ftpS)
                                .withHr(hrMax)
                                .withDate(getDaysTo(activityDate)));
                    }
                    checkFtp();
                }
            }

            handler.post(eventListener::onActivitySynced);
        });
    }

    private void setCurrentDetail() {

        long d = getDaysTo(activityDate);

        athlete.getDetailList().sort(Comparator.reverseOrder());
        AthleteDetail tmpAthleteDetail = athlete.getDetailList().get(0);
        if (d - tmpAthleteDetail.getDate() < ftpDays)
            athleteDetail = tmpAthleteDetail;
        else
            calculateAthleteDetail();


        long maxD = 0;
        for (AthleteDetail detail : athlete.getDetailList()) {
            if (detail.getDate() <= d && detail.getDate() > maxD) {
                maxD = detail.getDate();
            }
        }
        if (maxD > 0) {
            for (AthleteDetail detail : athlete.getDetailList()) {
                if (detail.getDate() == maxD) {
                    athleteDetail = detail;
                    return;
                }
            }
        }
        athleteDetail = new AthleteDetail(100,182,65, d, d);
    }

    private void calculateAthleteDetail() {

    }

    private void checkFtp() {
        int ftpDays = 90;
        int hrDays = 180;
        long millis = activityDate.getTimeInMillis();
        Calendar ftpC = Calendar.getInstance();
        ftpC.setTimeInMillis(millis);
        ftpC.add(Calendar.DATE, -ftpDays);
        Calendar hrC = Calendar.getInstance();
        hrC.setTimeInMillis(millis);
        hrC.add(Calendar.DATE, -hrDays);
        long ftpD = getDaysTo(ftpC);
        long hrD = getDaysTo(hrC);
        int maxFtp = ftpS;
        int maxHr = hrMax;
        for (AthleteDetail detail : athlete.getDetailList()) {
            if (detail.getDate() >= ftpD)
                maxFtp = Math.max(maxFtp, detail.getFtp());
            if (detail.getDate() > hrD)
                maxHr = Math.max(maxHr, detail.getHrm());
        }
        for (Ftp f : athlete.getFtpList()) {
            if (f.getDate() >= ftpD)
                maxFtp = Math.max(maxFtp, f.getFtp());
            if (f.getDate() > hrD)
                maxHr = Math.max(maxHr, f.getHr());
        }

        if (athleteDetail != null) {
            if (athleteDetail.getHrm() != maxHr) {
                athleteDetail.setHrm(maxHr);
                updateList(athlete.getDetailList(), athleteDetail);
            }
            if (athleteDetail.getFtp() != maxFtp) {
                long id = getDaysTo(activityDate);
                AthleteDetail detail = new AthleteDetail(maxFtp, athleteDetail.getHrm(),
                        athleteDetail.getHrr(), id, id);
                updateList(athlete.getDetailList(), detail);
            }
        }
    }

    private void setFtp(int ftp) {
        int cntA = 20*60;
        int cntL = 45*60;
        ftpAS.add(ftp);
        ftpAL.add(ftp);
        if (ftpAS.size() >= cntA) {
            int ftpTmp = Math.round((float)getAvg(ftpAS)*0.95f);
            if (ftpTmp > ftpS)
                ftpS = ftpTmp;
            if (ftpAS.size() > cntA)
                ftpAS.remove(0);
        }
        if (ftpAL.size() >= cntA) {
            int ftpTmp = getAvg(ftpAL);
            if (ftpTmp > ftpL)
                ftpL = ftpTmp;
            if (ftpAL.size() > cntL)
                ftpAL.remove(0);
        }
    }

    private int getAvg(List<Integer> inList) {
        int tot = 0;
        for (Integer i : inList)
            tot += i;
        return Math.round( (float)tot/ (float) inList.size());
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
