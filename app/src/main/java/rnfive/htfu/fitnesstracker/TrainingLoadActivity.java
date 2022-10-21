package rnfive.htfu.fitnesstracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import rnfive.htfu.fitnesstracker.athlete.fitness.Fitness;
import rnfive.htfu.fitnesstracker.strava.StravaActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import lombok.Getter;
import lombok.ToString;

import static rnfive.htfu.fitnesstracker.MainActivity.bDarkMode;
import static rnfive.htfu.fitnesstracker.MainActivity.athlete;
import static rnfive.htfu.fitnesstracker.util.Constants.getPxFromDp;

public class TrainingLoadActivity extends AppCompatActivity {

    private static final String TAG = "TrainingLoadActivity";

    private final int[][] dayOffset = {
            {DayOfWeek.MONDAY.getValue(), 0},
            {DayOfWeek.TUESDAY.getValue(), 1},
            {DayOfWeek.WEDNESDAY.getValue(), 2},
            {DayOfWeek.THURSDAY.getValue(), 3},
            {DayOfWeek.FRIDAY.getValue(), 4},
            {DayOfWeek.SATURDAY.getValue(), 5},
            {DayOfWeek.SUNDAY.getValue(), 6}
    };

    private static final Week[] history = new Week[52];
    private ImageView ivLoadChart;

    private int ivW;
    private int ivH;

    private final Map<Float, Float> dpMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_load);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator((bDarkMode?R.drawable.ic_arrow_back_white_24dp:R.drawable.ic_arrow_back_black_24dp));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.training_load);
        }

        loadDpMap(this);

        // TODO - load training history
        loadTrainingHistory();

        ivLoadChart = findViewById(R.id.fitness_curves);
        ViewTreeObserver vtoPv = ivLoadChart.getViewTreeObserver();
        vtoPv.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivH = ivLoadChart.getHeight();
                ivW = ivLoadChart.getWidth();
                ViewTreeObserver obsPv = ivLoadChart.getViewTreeObserver();
                obsPv.removeOnGlobalLayoutListener(this);
                drawCurve();
            }
        });
    }

    private void loadDpMap(Context context) {
        for (float f = 0.1f; f <= 5.0; f += 0.1f) {
            float px = getPxFromDp(f, context);
            dpMap.put(f, px);
        }
    }

    private Float getPx(float dp) {
        Float ret = null;
        if (dpMap.containsKey(dp))
            ret = dpMap.get(dp);
        return (ret == null?1.0f:ret);
    }

    private void loadTrainingHistory() {
        resetHistory();
        LocalDate ld = LocalDate.now();
        DayOfWeek today = ld.getDayOfWeek();
        long dt_today = ld.toEpochDay();
        long dt_start = dt_today - getOffset(today) - 1;
        Log.d(TAG, "Today: " + today + " : " + dt_today + " : " + dt_start);
        for (Fitness fitness : athlete.getFitnessList()) {
        // for (int i=0;i<fitnessArray.size();i++) {
            // long key = fitnessArray.keyAt(i);
            // Fitness fitness = fitnessArray.get(key);
            long delta = dt_start - fitness.getId();
            int wk = (int) (delta/7 + 1);
            if (wk < 52) {
                if (dt_start < fitness.getId())
                    wk = 0;

                Week week = history[wk];

                if (fitness.getStressScores().size() > 0) {
                    for (Fitness.StressScore ss : fitness.getStressScores()) {
                        int val = (ss.getPwrStressScore() == null || ss.getPwrStressScore() < ss.getHrStressScore() ? ss.getHrStressScore() : ss.getPwrStressScore());
                        if (getActivityType(ss.getId()).contains("Ride"))
                            week.addBike(val);
                        else
                            week.addRun(val);
                        week.addTotal(val);
                    }
                }
                history[wk] = week;
            }
        }

    }

    private int getOffset(DayOfWeek dof) {
        int val = dof.getValue();
        for (int[] map : dayOffset) {
            if (map[0] == val)
                return map[1];
        }
        return 0;
    }

    private void drawCurve() {
        Bitmap bitmap = Bitmap.createBitmap(ivW, ivH, Bitmap.Config.ARGB_8888);
        Canvas fitnessCurves = new Canvas(bitmap);

        int xMarginL = 70;
        int xMarginR = 15;
        int xMargin = xMarginL + xMarginR;
        int yMarginB = 35 + ivH / 30;
        int yMarginT = 20;

        int weeks = 20; // TODO - add spinner functionality;
        float xWeek = (ivW - xMargin) / (float) weeks;

        double dMaxVal = 0d;
        for (Week week : history) {
            dMaxVal = Math.max(dMaxVal, week.getTotal());
        }

        int iHorizSpacing = 50;
        dMaxVal = ((int) (dMaxVal/(double)iHorizSpacing) + 0.5f) * iHorizSpacing;
        int iHorizSpaces = (int) (dMaxVal) / iHorizSpacing ;
        float yHoriz = (float) (ivH - yMarginB - yMarginT) / (float) iHorizSpaces;

        Paint paintS = new Paint();
        paintS.setStyle(Paint.Style.STROKE);
        paintS.setColor(getColor((bDarkMode ? R.color.white : R.color.black)));
        paintS.setStrokeWidth(getPx(1.0f));

        Paint paintG = new Paint();
        paintG.setStyle(Paint.Style.STROKE);
        paintG.setAlpha(50);
        paintG.setColor(getColor(R.color.gray));
        paintG.setStrokeWidth(getPx(0.5f));
        paintG.setTextSize(yHoriz * 0.8f);

        Paint paintT = new Paint();
        paintT.setStyle(Paint.Style.FILL);
        paintT.setAlpha(150);
        paintT.setColor(getColor(R.color.colorPrimaryDark));
        paintT.setTextSize(ivH / 25f);
        paintT.setTextAlign(Paint.Align.RIGHT);

        // Draw horizontal lines
        int yStart = ivH - yMarginB;
        Path hLine;
        for (int i = 0; i <= iHorizSpaces; i++) {
            hLine = new Path();
            hLine.moveTo(xMarginL, yStart - i * yHoriz);
            hLine.lineTo(ivW - xMarginR, yStart - i * yHoriz);
            hLine.close();
            fitnessCurves.drawPath(hLine, i == 0 ? paintS : paintG);
            fitnessCurves.drawText(String.valueOf(i * iHorizSpacing), xMarginL - xMarginR, yStart - i * yHoriz + (ivH / 25f) / 2.9f, paintT);
        }

        // Draw vertical lines
        //LocalDate ld = LocalDate.now();
        //String sMonth = ld.format(DateTimeFormatter.ofPattern("MMM"));
        //paintT.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i <= weeks; i++) {
            if (i%2 == 0) {
                hLine = new Path();
                hLine.moveTo(xMarginL + i * xWeek, yMarginT);
                hLine.lineTo(xMarginL + i * xWeek, ivH - yMarginB);
                hLine.close();
                fitnessCurves.drawPath(hLine, i == 0 ? paintS : paintG);
            }
            //String cMon = ld.plusDays(-i* 7L).format(DateTimeFormatter.ofPattern("MMM"));
            //if (!sMonth.equals(cMon) && i < weeks) {
            //    fitnessCurves.drawText(sMonth, ivW - xMarginR - i * xWeek, ivH, paintT);
            //    sMonth = cMon;
            //}
        }

        LocalDate ld = LocalDate.now();
        String sMonth = ld.format(DateTimeFormatter.ofPattern("MMM"));
        paintT.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i <= weeks*30; i++) {
            String cMon = ld.plusDays(-i).format(DateTimeFormatter.ofPattern("MMM"));
            if (!sMonth.equals(cMon) && i < (weeks-1)*30) {
                fitnessCurves.drawText(sMonth, ivW - xMarginR - i * (xWeek/7), ivH - ivH / 50f, paintT);
                sMonth = cMon;
            }
        }

        // Get background color
        Paint paintC = new Paint();
        paintC.setStyle(Paint.Style.FILL);
        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            int color = a.data;
            paintC.setColor(color);
            Log.d(TAG, "background found : " + Color.valueOf(-13619152));
        }


        Paint paintF = new Paint();
        paintF.setStyle(Paint.Style.STROKE);
        paintF.setColor(getColor(R.color.fitness));
        paintF.setStrokeWidth(getPx(1.5f));
        float yLoad = (float) (ivH - yMarginB - yMarginT) / (float) dMaxVal;
        Path load = new Path();
        Path bike = new Path();
        Path run = new Path();
        for (int i = 0; i <= weeks; i++) {
            Week week = history[i];
            if (i == 0) {
                load.moveTo(ivW - xMarginR - (i * xWeek), ivH - yMarginB - yLoad * week.getTotal());
                bike.moveTo(ivW - xMarginR - (i * xWeek), ivH - yMarginB - yLoad * week.getBike());
                run.moveTo(ivW - xMarginR - (i * xWeek), ivH - yMarginB - yLoad * week.getRun());
            } else {
                load.lineTo(ivW - xMarginR - (i * xWeek), ivH - yMarginB - yLoad * week.getTotal());
                bike.lineTo(ivW - xMarginR - (i * xWeek), ivH - yMarginB - yLoad * week.getBike());
                run.lineTo(ivW - xMarginR - (i * xWeek), ivH - yMarginB - yLoad * week.getRun());
            }
        }
        paintF.setColor(getColor(R.color.fatigue));
        paintF.setStrokeWidth(getPx(1.2f));
        fitnessCurves.drawPath(bike, paintF);
        paintF.setColor(getColor(R.color.form));
        fitnessCurves.drawPath(run, paintF);

        paintF.setColor(getColor(R.color.fitness));
        paintF.setStrokeWidth(getPx(1.5f));
        fitnessCurves.drawPath(load, paintF);
        for (int i = 0; i <= weeks; i++) {
            Week week = history[i];
            paintF.setStrokeWidth(getPx(1.2f));
            paintF.setColor(getColor(R.color.form));
            fitnessCurves.drawCircle(ivW-xMarginR-(i*xWeek), ivH-yMarginB-yLoad*week.getRun(), getPx(2.0f), paintC);
            fitnessCurves.drawCircle(ivW-xMarginR-(i*xWeek), ivH-yMarginB-yLoad*week.getRun(), getPx(2.0f), paintF);
            paintF.setColor(getColor(R.color.fatigue));
            fitnessCurves.drawCircle(ivW-xMarginR-(i*xWeek), ivH-yMarginB-yLoad*week.getBike(), getPx(2.0f), paintC);
            fitnessCurves.drawCircle(ivW-xMarginR-(i*xWeek), ivH-yMarginB-yLoad*week.getBike(), getPx(2.0f), paintF);
            paintF.setColor(getColor(R.color.fitness));
            paintF.setStrokeWidth(getPx(1.5f));
            fitnessCurves.drawCircle(ivW-xMarginR-(i*xWeek), ivH-yMarginB-yLoad*week.getTotal(), getPx(3.0f), paintC);
            fitnessCurves.drawCircle(ivW-xMarginR-(i*xWeek), ivH-yMarginB-yLoad*week.getTotal(), getPx(3.0f), paintF);
        }


            // MONDAY vertical lines
            /*
            if (a.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Path tdLine = new Path();
                tdLine.moveTo((x - xD / 2f), fY1);
                tdLine.lineTo((x - xD / 2f), fY2);
                fitnessCurves.drawPath(tdLine, paintS);
            }

             */

        ivLoadChart.setImageBitmap(bitmap);
    }


    static void resetHistory() {
        for (int i = 0; i < 52; i++)
            history[i] = new Week(i);
    }

    private String getActivityType(long activityId) {
        for (StravaActivity activity : athlete.getActivityList()) {
            if (activity.getId() == activityId) {
                Log.d(TAG, "Found activity [" + activity.getId() + "] Type [" + activity.getActivityType() + "]");
                return activity.getActivityType();
            }
        }
        return "Ride";
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

    @Getter
    @ToString
    private static class Week {
        private final int id;
        private int total;
        private int bike;
        private int run;

        public Week(int id) {
            this.id = id;
        }

        public void addTotal(int val) {
            this.total += val;
        }

        public void addBike(int val) {
            this.bike += val;
        }

        public void addRun(int val) {
            this.run += val;
        }
    }
}
