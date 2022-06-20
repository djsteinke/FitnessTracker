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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.rn5.fitnesstracker.MainActivity.athlete;
import static com.rn5.fitnesstracker.MainActivity.dayInMS;
import static com.rn5.fitnesstracker.util.Constants.sdf;
import static com.rn5.fitnesstracker.util.Constants.sdfDatePattern;
import static com.rn5.fitnesstracker.util.Constants.sdfPattern;
import static com.rn5.fitnesstracker.util.Constants.updateList;

public class StravaActivitiesExecutor {

    private static final String TAG = StravaActivitiesExecutor.class.getSimpleName();
    private final EventListener eventListener;
    private int hrMax = 0;
    private int ftpMax = 0;
    private static final int ftpDays = 90;
    private static final int hrDays = 180;
    private final List<Integer> ftpAS = new ArrayList<>();
    private final List<Integer> ftpAL = new ArrayList<>();
    private AthleteDetail athleteDetail;
    private long toEpochDay;
    private long toEpochMillis;

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
                long after = (athlete.getLastUpdateTime() - dayInMS) / 1000;
                activities = api.getActivities(after, gmtDate.getTimeInMillis() / 1000).execute();
            } catch (StravaAPIException e) {
                Log.e(TAG,"StravaAPIException[" + e.getMessage() + "]");
            }
            Log.d(TAG,"api.getActivities()");

            List<Double> pow30 = new ArrayList<>();
            if (activities != null) {
                Collections.sort(activities);
                for (Activity activity : activities) {
                    Log.d(TAG, "Activity Type: " + activity.getType());
                    ftpAS.clear();
                    ftpAL.clear();
                    ftpMax = 0;
                    StreamAPI streamAPI = new StreamAPI(config);
                    Stream stream = null;
                    try {
                        stream = streamAPI.getStreams(activity.getId()).execute();
                    } catch (StravaAPIException e) {
                        Log.d(TAG, "Activity " + activity.getId() + "/" + activity.getName() + " does not have streams.");
                    }

                    if (stream != null) {

                        LocalDate ld = LocalDate.parse(activity.getStartDate(), DateTimeFormatter.ofPattern(sdfPattern));
                        String ldStr = ld.format(DateTimeFormatter.ofPattern(sdfDatePattern, Locale.US));

                        Date date = new Date();
                        try {
                            date = sdf.parse(activity.getStartDateUtc());
                        } catch (ParseException e) {
                            Log.e(TAG, "Parse exception from Strava activity UTC start date");
                        }

                        toEpochDay = ld.toEpochDay();
                        toEpochMillis = date.getTime();
                        setCurrentDetail();

                        Log.d(TAG, "toEpochDay: " + toEpochDay);
                        Log.d(TAG, athleteDetail.toString());


                        double ftp = athleteDetail.getFtp();
                        double hrm = athleteDetail.getHrm();
                        double hrr = athleteDetail.getHrr();

                        Log.d(TAG, "Activity Date[" + ldStr + "] DATE[" + toEpochMillis +
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
                                        setFtp((int) Math.round(val));
                                        pow30.add(0, val);
                                        if (pow30.size() > 30)
                                            pow30.remove(30);
                                        psc++;
                                        powTot += val;
                                        rollAvgPow += Math.pow(getAvg30(pow30), 4);
                                    }
                                }
                                i++;
                            }
                        }
                        double wp = Math.pow((rollAvgPow / psc), 0.25d);
                        double intensity = wp / ftp;
                        Log.d(TAG, "Weighted Power [" + wp + "] Intensity [" + intensity + "]");
                        double pss = wp * (wp / ftp) * st / (ftp * 3600d) * 100d;
                        Log.d(TAG, "StartDateLocal:" + date);
                        int iPSS = (int) Math.round(pss);
                        double powAvg = (psc > 0 ? powTot / psc : 0.0d);

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
                                hrMax = (int) Math.max(hrMax, val);
                                hrtot += val;
                                hrsc++;
                                hrR = (val - hrr) / (hrm - hrr);
                                trimp += (thisT - lastT) / 60d * hrR * 0.64d * Math.exp(1.92d * hrR);
                                lastT = thisT;
                                i++;
                            }
                        }
                        double hravg = (hrsc > 0 ? hrtot / hrsc : 0.0d);
                        //hrr = (hravg-hrr)/(hrm-hrr);
                        //trimp = (st/60d)*hrr*0.64d*Math.exp(1.92d*hrr);
                        double hrss = (int) (trimp / getHRatLTHR() * 100d);
                        int iHRSS = (int) Math.round(hrss);
                        Log.d(TAG, "TRIMP [" + trimp + "] HRSS [" + hrss + "] HRAvg[" + hravg + "]");

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
                                .withPss(iPSS)
                                .withHrss(iHRSS)
                                .withDate(toEpochDay);
                        stravaActivity.setPwrFtp(ftpMax);
                        stravaActivity.setDistance(distance);
                        stravaActivity.setMovingTime(activity.getMovingTime());
                        stravaActivity.setHrAvg((int) hravg);
                        stravaActivity.setPwrAvg((int) powAvg);
                        stravaActivity.setActivityType(activity.getType());
                        updateList(athlete.getActivityList(), stravaActivity);
                        Log.e("PSS", "PSS[" + iPSS + "]");
                        if (ftpMax > 0) {
                            athlete.getFtpList().add(new Ftp()
                                    .withFtp(ftpMax)
                                    .withHr(hrMax)
                                    .withDate(toEpochDay));
                        }
                        checkFtp();
                    }
                }
            }

            handler.post(eventListener::onActivitySynced);
        });
    }

    private void setCurrentDetail() {
        athlete.getDetailList().sort(Comparator.reverseOrder());
        AthleteDetail tmpAthleteDetail = athlete.getDetailList().get(0);
        if (toEpochDay - tmpAthleteDetail.getId() <= ftpDays)
            athleteDetail = tmpAthleteDetail;
        else
            calculateAthleteDetail();
    }

    private void calculateAthleteDetail() {
        long ftpD = toEpochDay - ftpDays;
        int maxFtp = 150;
        int maxHr = determineMaxHR();
        int restHr = 65;
        long maxFtpDt = toEpochDay;

        for (Ftp f : athlete.getFtpList()) {
            if (f.getDate() >= ftpD && maxFtp < f.getFtp()) {
                maxFtp = f.getFtp();
                maxFtpDt = f.getDate();
            }
        }

        athleteDetail = new AthleteDetail(maxFtp, maxHr, restHr, maxFtpDt);
        updateList(athlete.getDetailList(), athleteDetail);
    }

    private int determineMaxHR() {
        int birthYear = 1983;
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int max = (220-(currentYear-birthYear));

        long hrD = toEpochDay - hrDays;
        for (Ftp f : athlete.getFtpList()) {
            if (f.getDate() >= hrD)
                max = Math.max(max, f.getHr());
        }
        return max;
    }

    private void checkFtp() {
        int hrm = Math.max(determineMaxHR(), hrMax);
        if (athleteDetail != null) {
            boolean update = false;
            if (athleteDetail.getHrm() != hrm) {
                athleteDetail.setHrm(hrm);
                update = true;
            }
            if (athleteDetail.getFtp() < ftpMax) {
                long id = toEpochDay;
                if (athleteDetail.getId() == id)
                    athleteDetail.setFtp(ftpMax);
                else
                    athleteDetail = new AthleteDetail(ftpMax, hrm, athleteDetail.getHrr(), id);
                update = true;
            }
            if (update)
                updateList(athlete.getDetailList(), athleteDetail);
        } else
            calculateAthleteDetail();
    }

    private void setFtp(int ftp) {
        int cntA = 20*60;
        int cntL = 45*60;
        ftpAS.add(ftp);
        ftpAL.add(ftp);
        if (ftpAS.size() >= cntA) {
            int ftpTmp = Math.round((float)getAvg(ftpAS)*0.95f);
            ftpMax = Math.max(ftpTmp, ftpMax);
            if (ftpAS.size() > cntA)
                ftpAS.remove(0);
        }
        if (ftpAL.size() >= cntA) {
            int ftpTmp = getAvg(ftpAL);
            ftpMax = Math.max(ftpTmp, ftpMax);
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
