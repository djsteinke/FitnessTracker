package com.rn5.fitnesstracker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.rn5.fitnesstracker.R;
import com.rn5.fitnesstracker.async.StravaAuthenticationAsync;
import com.rn5.fitnesstracker.define.EventListener;
import com.rn5.fitnesstracker.define.FitnessListAdapter;
import com.rn5.fitnesstracker.executor.StravaActivitiesExecutor;
import com.rn5.fitnesstracker.executor.StravaAuthenticationExecutor;
import com.rn5.fitnesstracker.model.AthleteData;
import com.rn5.fitnesstracker.model.Fitness;
import com.rn5.fitnesstracker.model.StravaActivity;
import com.rn5.fitnesstracker.model.StravaToken;
import com.rn5.libstrava.authentication.model.AuthenticationType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.rn5.fitnesstracker.define.Constants.*;
import static com.rn5.libstrava.common.model.Constants.TOKEN;

public class MainActivity extends AppCompatActivity implements EventListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static AthleteData athleteData;

    private ImageView ivFitnessCurve;
    private int fcW;
    private int fcH;
    public static boolean bDarkMode = false;
    private int graphDays = 42;
    private boolean bReadyToDraw = false;
    private DisplayMetrics displayMetrics;

    public static final int dayInMS = 86400000;
    private final List<Fitness> rvaDataset = new ArrayList<>();
    private FitnessListAdapter rvAdapter;

    public static LongSparseArray<StravaActivity> activityArray = new LongSparseArray<>();
    public static LongSparseArray<Fitness> fitnessArray = new LongSparseArray<>();

    public static final int menu_strava_login = R.id.strava_login;
    public static final int menu_athlete_details = R.id.athlete_details;
    public static final int menu_sync_today = R.id.sync_today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFilePath();
        STRAVA_TOKEN = new StravaToken().fromFile();
        athleteData = AthleteData.loadFromFile();
        processAthleteData(true);

        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        bDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        bReadyToDraw = false;

        RecyclerView rvFitness = findViewById(R.id.recyclerView);
        ivFitnessCurve = findViewById(R.id.fitness_curves);
        ViewTreeObserver vtoPv = ivFitnessCurve.getViewTreeObserver();
        vtoPv.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fcH = ivFitnessCurve.getHeight();
                fcW = ivFitnessCurve.getWidth();
                ViewTreeObserver obsPv = ivFitnessCurve.getViewTreeObserver();
                obsPv.removeOnGlobalLayoutListener(this);
                drawFitnessCurves();
                bReadyToDraw = true;
            }
        });

        Button request = findViewById(R.id.button);
        request.setOnClickListener(view -> {
            athleteData.setLastUpdateTime(athleteData.getLastUpdateTime()-dayInMS);
            syncActivities();
        });

        Button calcFitness = findViewById(R.id.calcFitness);
        calcFitness.setOnClickListener(view -> calculateFitness());

        rvFitness.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvFitness.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        setDataset();
        rvAdapter = new FitnessListAdapter(rvaDataset, this);
        rvFitness.setAdapter(rvAdapter);

        Spinner spinner = findViewById(R.id.spinner);
        SpinnerAdapter sAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_values, android.R.layout.simple_spinner_item);
        //sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int[] days = new int[]{30,42,60,90,120,150,180};
                graphDays = days[i];
                drawFitnessCurves();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void processAthleteData(boolean both) {
        activityArray = new LongSparseArray<>();
        for (StravaActivity activity : athleteData.getActivityList())
            activityArray.put(activity.getId(), activity);
        if (both) {
            fitnessArray = new LongSparseArray<>();
            for (Fitness fitness : athleteData.getFitnessList())
                fitnessArray.put(fitness.getId(), fitness);
        }
    }

    private void calculateFitness() {
        Log.d(TAG,"calcFitness pressed");

        /*
        if (athleteData.getFitnessList() == null || athleteData.getFitnessList().size() == 0) {
            LongSparseArray<Fitness> newArray = new LongSparseArray<>();
            athleteData.setFitnessList(newArray);
        }

         */

        int fDays = 7;
        int aDays = 180;

        Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        //Calendar gmtDate = Calendar.getInstance();
        int iDaySinceUp = (int) ((gmtDate.getTimeInMillis() -  athleteData.getLastUpdateTime())/dayInMS);
        aDays = (aDays<iDaySinceUp?aDays:iDaySinceUp+2);
        Log.d(TAG,"calculateFitness : aDays[" + aDays + "]");

        Calendar date = Calendar.getInstance();
        long today = getDaysTo(date);
        date.add(Calendar.DATE,-(aDays+1));
        long dayBeforeLastUpdate = getDaysTo(date);
        //for (Map.Entry<Long,StravaActivity> entry : athleteData.getActivityList().entrySet()) {
        long lastDt = 0;
        Calendar c = Calendar.getInstance();
        for (int i=0;i<activityArray.size();i++) {
        //for (int i=aDays;i>-1;i--) {
          //  long key = today-i;
            long key = activityArray.keyAt(i);
            StravaActivity activity = activityArray.get(key);
            //Log.d(TAG,"StravaActivity ID[" + entry.getValue().getId() + "]");
            c.setTimeInMillis(activity.getDate());
            //long dt = (int) (activity.getDate()/ dayInMS);
            long dt = getDaysTo(c);
            if (dt > dayBeforeLastUpdate) {
                if (dt != lastDt)
                    fitnessArray.put(dt,null);
                if (fitnessArray.get(dt, null) != null) {
                    Fitness fitness = fitnessArray.get(dt);
                    if (fitness != null) {
                        Integer ss = fitness.getStressScore();
                        Integer hrss = fitness.getHrStressScore();
                        ss += activity.getFtpEffort();
                        hrss += activity.getHrEffort();
                        fitness.setStressScore(ss);
                        fitness.setHrStressScore(hrss);
                        fitnessArray.put(dt, fitness);
                    }
                } else {
                    Fitness fitness = new Fitness(activity.getFtpEffort(), activity.getHrEffort(), 0d, 0d, 0d, dt);
                    athleteData.getFitnessList().add(fitness);
                    fitnessArray.put(dt, fitness);
                }
            }
            lastDt = dt;
        }
        Double dFit = 0d;
        Double dFat = 0d;

        if (fitnessArray.get(dayBeforeLastUpdate,null) != null) {
        //if (athleteData.getFitnessList().containsKey(today)) {
            Fitness fitness = fitnessArray.get(dayBeforeLastUpdate);
            if (fitness != null) {
                Log.d(TAG,"calculateFitness() get previous fitness [" + dayBeforeLastUpdate + "]");
                dFit = fitness.getFitness();
                dFat = fitness.getFatigue();
            }
        }

        for (int i=aDays;i>-(fDays+1);i--) {
            long dt = today-i;
            //Log.d(TAG,"today[" + dt + "] i[" + i + "]");
            int pss = 0;
            int hrss = 0;
            if (fitnessArray.get(dt,null) != null) {
            //if (athleteData.getFitnessList().containsKey(dt)) {
                Fitness fitness = fitnessArray.get(dt);
                if (fitness != null) {
                    pss = fitness.getStressScore();
                    hrss = fitness.getHrStressScore();
                }
            }
            Double fit = dFit + (pss - dFit) * (1 - Math.exp((-1d / 42d)));
            Double fatigue = dFat + (pss - dFat) * (1 - Math.exp((-1d / 7d)));
            Fitness fitness = new Fitness(pss,hrss,fit,fatigue,dFit-dFat, dt);
            fitnessArray.put(dt,fitness);
            dFit = fit;
            dFat = fatigue;
            /*else {
                Double fit = dFit + (0 - dFit) * (1-Math.exp((-1d / 42d)));
                Double fatigue = dFat + (0 - dFat) * (1-Math.exp((-1d / 7d)));
                Fitness fitness = new Fitness(0,fit,fatigue,dFit-dFat, dt);
                athleteData.getFitnessList().put(dt,fitness);
                dFit = fit;
                dFat = fatigue;

            }

             */
        }
        athleteData.updateFitnessList();
        athleteData.save();
        setDataset();
        drawFitnessCurves();
        rvAdapter.notifyDataSetChanged();
    }

    private void drawFitnessCurves() {
        if (bReadyToDraw) {
            Bitmap bitmap = Bitmap.createBitmap(fcW, fcH, Bitmap.Config.ARGB_8888);
            Canvas fitnessCurves = new Canvas(bitmap);


            //Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            Calendar calendar = Calendar.getInstance();
            long today = calendar.getTimeInMillis() / dayInMS;
            int xMarginL = 70;
            int xMarginR = 10;
            int xMargin = xMarginL + xMarginR;

            float aDays = graphDays;
            float fDays = 7f;
            float days = aDays + fDays;
            float xD = (fcW - xMargin) / days;

            double dMaxVal = 0d;
            double dMinVal = 0d;
            for (int i = (int) aDays + 1; i > -1; i--) {
                long dt = today - i;
                Fitness fitness = fitnessArray.get(dt);
                if (fitness != null) {
                    Double dFit = fitness.getFitness();
                    Double dFat = fitness.getFatigue();
                    Double dForm = fitness.getForm();

                    if (dFit > dMaxVal) dMaxVal = dFit;
                    if (dFat > dMaxVal) dMaxVal = dFat;
                    if (dForm < dMinVal) dMinVal = dForm;
                }
            }

            int iTens = (int) (dMaxVal - dMinVal) / 10 + 2;
            float h10 = (float) fcH / (float) iTens;
            int iZero = (int) dMaxVal / 10 + 1;

            Paint paintS = new Paint();
            paintS.setStyle(Paint.Style.STROKE);
            paintS.setColor(getColor((bDarkMode ? R.color.white : R.color.black)));
            paintS.setStrokeWidth(getPxFromDp(1.0f));

            Paint paintC = new Paint();
            paintC.setStyle(Paint.Style.STROKE);
            paintC.setColor(getColor(R.color.white));
            paintC.setStrokeWidth(getPxFromDp(1.0f));

            Paint paintG = new Paint();
            paintG.setStyle(Paint.Style.STROKE);
            paintG.setAlpha(50);
            paintG.setColor(getColor(R.color.gray));
            paintG.setStrokeWidth(getPxFromDp(0.5f));
            paintG.setTextSize(h10 * 0.8f);

            Paint paintT = new Paint();
            paintT.setStyle(Paint.Style.FILL);
            paintT.setAlpha(150);
            paintT.setColor(getColor(R.color.colorPrimaryDark));
            paintT.setTextSize(fcH / 25f);
            paintT.setTextAlign(Paint.Align.RIGHT);

            Paint paintF = new Paint();
            paintF.setStyle(Paint.Style.STROKE);
            paintF.setAlpha(100);
            paintF.setStrokeWidth(getPxFromDp(0.8f));

            // Draw horizontal lines
            Path hLine;
            for (int i = 1; i < iTens; i++) {
                hLine = new Path();
                hLine.moveTo(xMarginL, i * h10);
                hLine.lineTo(fcW - xMarginR, i * h10);
                hLine.close();
                fitnessCurves.drawPath(hLine, i == iZero ? paintS : paintG);
                fitnessCurves.drawText(String.valueOf((iZero - i) * 10), xMarginL - xMarginR, i * h10 + (fcH / 25f) / 2.9f, paintT);
            }

            float y0 = h10 * (float) iZero;
            float[] formZones = new float[]{25f, 5f, -10f, -30f};
            paintS.setAlpha(200);

            // Form Zones horizontal lines
            int dashCnt = 43;
            float dashW = (fcW - xMargin) / (float) dashCnt;
            for (float val : formZones) {
                hLine = new Path();
                hLine.moveTo(xMarginL, y0 - val / 10f * h10);
                for (int i = 0; i <= dashCnt; i++) {
                    if (i % 2 != 0)
                        hLine.lineTo(xMarginL + (float) i * dashW, y0 - val / 10f * h10);
                    else {
                        hLine.moveTo(xMarginL + (float) i * dashW, y0 - val / 10f * h10);
                        if (i > 0)
                            fitnessCurves.drawCircle(xMarginL + (float) i * dashW - dashW / 2f, y0 - val / 10f * h10, paintS.getStrokeWidth() / 2f, paintS);
                    }
                }
                fitnessCurves.drawPath(hLine, paintS);
            }

            Path ftLine = new Path();
            Path fgLine = new Path();
            Path fmLine = new Path();

            float fY1 = (float) iZero * h10 + fcH / 45f;
            float fY2 = (float) iZero * h10 - fcH / 45f;
            Calendar c = Calendar.getInstance();
            Calendar a = Calendar.getInstance();
            a.add(Calendar.DATE, (int) -aDays);

            ftLine.moveTo(xMarginL, (int) y0);
            fgLine.moveTo(xMarginL, (int) y0);
            fmLine.moveTo(xMarginL, (int) y0);
            float yFtP = 0.0f;
            float yFgP = 0.0f;
            float yFmP = 0.0f;
            c.add(Calendar.DATE, (int) fDays);
            for (int i = (int) days; i > -1; i--) {
                long dt = getDaysTo(a);

                float x = xMarginL + (int) ((days - i) * xD);
                // TODAY vertical line
                if (dt == getDaysTo(Calendar.getInstance())) {
                    Path tdLine = new Path();
                    tdLine.moveTo(x, 0);
                    tdLine.lineTo(x, fcH);
                    fitnessCurves.drawPath(tdLine, paintG);
                }

                // MONDAY vertical lines
                if (a.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    Path tdLine = new Path();
                    tdLine.moveTo((x - xD / 2f), fY1);
                    tdLine.lineTo((x - xD / 2f), fY2);
                    fitnessCurves.drawPath(tdLine, paintS);
                }

                Fitness fitness = fitnessArray.get(dt);
                if (fitness != null) {
                    float yFt = (float) (y0 - (fitness.getFitness() / 10.0 * h10));
                    float yFg = (float) (y0 - (fitness.getFatigue() / 10.0 * h10));
                    float yFm = (float) (y0 - (fitness.getForm() / 10.0 * h10));
                    if (i == days) {
                        ftLine.moveTo(x, yFt);
                        fgLine.moveTo(x, yFg);
                        fmLine.moveTo(x, yFm);
                    } else {
                        if (i < fDays) {
                            ftLine.lineTo(x - xD / 2f, (yFtP + yFt) / 2f);
                            ftLine.moveTo(x, yFt);

                            float yFgD = yFg - yFgP;
                            float yFmD = yFm - yFmP;
                            fgLine.lineTo(x - xD * 3f / 4f, yFg - yFgD * 3f / 4f);
                            fmLine.lineTo(x - xD * 3f / 4f, yFm - yFmD * 3f / 4f);
                            fgLine.moveTo(x - xD / 2f, yFg - yFgD / 2f);
                            fmLine.moveTo(x - xD / 2f, yFm - yFmD / 2f);
                            fgLine.lineTo(x - xD / 4f, yFg - yFgD / 4f);
                            fmLine.lineTo(x - xD / 4f, yFm - yFmD / 4f);
                            fgLine.moveTo(x, yFg);
                            fmLine.moveTo(x, yFm);
                        } else {
                            ftLine.lineTo(x, yFt);
                            fgLine.lineTo(x, yFg);
                            fmLine.lineTo(x, yFm);
                        }
                    }
                    yFtP = yFt;
                    yFgP = yFg;
                    yFmP = yFm;

                    if (fitness.getStressScore() != null && fitness.getStressScore() > 0) {
                        paintS.setAlpha(255);
                        fitnessCurves.drawCircle(x, (float) y0, 4, paintS);
                    }
                } else {
                    Log.d(TAG, "fitness is null");
                }

                c.add(Calendar.DATE, -1);
                a.add(Calendar.DATE, 1);
            }
            paintF.setColor(getColor(R.color.fatigue));
            fitnessCurves.drawPath(fgLine, paintF);
            paintF.setColor(getColor(R.color.form));
            fitnessCurves.drawPath(fmLine, paintF);
            paintF.setColor(getColor(R.color.fitness));
            paintF.setAlpha(255);
            paintF.setStrokeWidth(getPxFromDp(1.5f));
            fitnessCurves.drawPath(ftLine, paintF);

            ivFitnessCurve.setImageBitmap(bitmap);
        }
    }

    private static Calendar getZeroTime() {
        SimpleDateFormat sdfZero = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        String sDate = "1970-01-02 00:00:01";
        Calendar z = Calendar.getInstance();
        try {
            z.setTime(sdfZero.parse(sDate));
        } catch (ParseException e) {
            Log.e(TAG,"getZeroTime " + e.getMessage());
        }
        return z;
    }

    public static int getDaysTo(Calendar c) {
        Calendar z = getZeroTime();
        long cMillis = c.getTimeInMillis();
        long zMillis = z.getTimeInMillis();
        return (int) ((cMillis-zMillis)/dayInMS + 1);
    }

    public void setDataset() {
        long today = getDaysTo(Calendar.getInstance());
        for (int i=0;i<fitnessArray.size();i++) {
            long key = fitnessArray.keyAt(i);
            if (key == today || key == today+1 || (key < today && fitnessArray.get(key).getStressScore() > 0)) {
                int fitIndex = getFitIndex(fitnessArray.get(key));
                if (fitIndex >= 0) {
                    rvaDataset.set(fitIndex,fitnessArray.get(key));
                } else {
                    rvaDataset.add(0, fitnessArray.get(key));
                }
            }
        }
    }

    private int getFitIndex(Fitness fitness) {
        for (Fitness fit : rvaDataset) {
            if (fit.getDate().equals(fitness.getDate()))
                return rvaDataset.indexOf(fit);
        }
        return -1;
    }

    private void setFilePath() {
        APP_FILE_PATH = this.getExternalFilesDir("Fitness Tracker");

        boolean result = APP_FILE_PATH != null;
        result = APP_FILE_PATH != null?APP_FILE_PATH.exists()||APP_FILE_PATH.mkdir():result;
        result = result?APP_FILE_PATH.canWrite()||APP_FILE_PATH.setWritable(true, true):result;
        Log.d(TAG,"setFilePath[" + result + "]");
    }

    private int getPxFromDp(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private void syncActivities() {
        Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        long nextUpdate = gmtDate.getTimeInMillis() - (5*60*1000);
        if (athleteData.getLastUpdateTime() == null || athleteData.getLastUpdateTime() < nextUpdate) {
            StravaActivitiesExecutor executor = new StravaActivitiesExecutor(this);
            executor.run();
            //StravaActivitiesAsync async = new StravaActivitiesAsync(this);
            //async.execute("na");
        }
    }

    @Override
    public void onToast(final String msg) {
        Looper.prepare();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    @Override
    public void onActivitySynced() {
        processAthleteData(false);
        Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        Log.d(TAG,"onActivitySynced()");
        Toast.makeText(this,"Activities Synced.",Toast.LENGTH_SHORT).show();
        calculateFitness();
        athleteData.setLastUpdateTime(gmtDate.getTimeInMillis());
        athleteData.save();
    }

    @Override
    public void onTokenRefreshed() {
        Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        String msg = "Token Refresh Failed.";
        if (TOKEN.getExpirationDate() > gmtDate.getTimeInMillis()) {
            msg = "Token Refreshed.";
            syncActivities();
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TOKEN != null) {
            Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            Log.d(TAG,"expire[" + TOKEN.getExpirationDate() + "] current[" + gmtDate.getTimeInMillis() + "]");
            if (TOKEN.getExpirationDate() < gmtDate.getTimeInMillis()) {
                new StravaAuthenticationExecutor(AuthenticationType.REFRESH_TOKEN, this, TOKEN.getRefreshToken()).run();

                //StravaAuthenticationAsync stravaAuth = new StravaAuthenticationAsync(AuthenticationType.REFRESH_TOKEN, this);
                //stravaAuth.execute(TOKEN.getRefreshToken());
            } else {
                Log.d(TAG,"syncActivities() TOKEN not expired.");
                syncActivities();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        if (bDarkMode) {
            menu.getItem(0).setIcon(R.drawable.ic_more_vert_white_24dp);
            menu.getItem(0).getSubMenu().getItem(0).setIcon(R.drawable.ic_person_white_24dp);
            menu.getItem(0).getSubMenu().getItem(1).setIcon(R.drawable.strava_icon_white);
            menu.getItem(0).getSubMenu().getItem(2).setIcon(R.drawable.ic_sync_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case menu_strava_login:
                this.startActivity(new Intent(this, StravaLogin.class));
                break;
            case menu_athlete_details:
                this.startActivity(new Intent(this, AthleteDetailsActivity.class));
                break;
            case menu_sync_today:
                Calendar gmtDate = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
                athleteData.setLastUpdateTime(gmtDate.getTimeInMillis() - dayInMS);
                syncActivities();
                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
