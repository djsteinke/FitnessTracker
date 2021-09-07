package com.rn5.fitnesstracker.athlete.fitness;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rn5.fitnesstracker.R;
import com.rn5.fitnesstracker.strava.StravaActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

import static com.rn5.fitnesstracker.MainActivity.athlete;
import static com.rn5.fitnesstracker.MainActivity.bDarkMode;
import static com.rn5.fitnesstracker.MainActivity.dayInMS;
import static com.rn5.fitnesstracker.util.Constants.mToMi;
import static com.rn5.fitnesstracker.util.Constants.sToTime;

public class FitnessListAdapter extends RecyclerView.Adapter<FitnessListAdapter.MyViewHolder> {
    private static final String TAG = FitnessListAdapter.class.getSimpleName();
    private static List<Fitness> mDataset = new ArrayList<>();

    private Context context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View vItem;
        MyViewHolder(View v) {
            super(v);
            vItem = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FitnessListAdapter(List<Fitness> inDataset, Context context) {
        mDataset = inDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public FitnessListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fitness_day, parent, false);
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final View vItem = holder.vItem;
        TextView date = vItem.findViewById(R.id.dt_value);
        if (!bDarkMode)
            date.setTextColor(context.getResources().getColor(R.color.black,null));
        TextView pss = vItem.findViewById(R.id.pss_value);
        TextView hrss = vItem.findViewById(R.id.hrss_value);
        TextView fitness = vItem.findViewById(R.id.fit_value);
        TextView fatigue = vItem.findViewById(R.id.fat_value);
        TextView form = vItem.findViewById(R.id.form_value);
        LinearLayout activityDetails = vItem.findViewById(R.id.ll_data);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        Calendar c = Calendar.getInstance();
        long dtMillis = mDataset.get(position).getDate()*dayInMS - c.getTimeZone().getRawOffset();
        c.setTimeInMillis(dtMillis);
        //Log.d(TAG,"Millis [" + dtMillis + "] Date [" + sdf.format(c.getTime()) +"]" + "rawOffset [" + c.getTimeZone().getRawOffset() + "]");

        date.setText(sdf.format(c.getTime()));
        pss.setText(String.valueOf(mDataset.get(position).getStressScore()));
        hrss.setText(String.valueOf(mDataset.get(position).getHrStressScore()));
        fitness.setText(getStringVal(mDataset.get(position).getFitness()));
        fatigue.setText(getStringVal(mDataset.get(position).getFatigue()));
        form.setText(getStringVal(mDataset.get(position).getForm()));
        long dtMax = dtMillis + dayInMS;
        StringBuilder sb_distance = new StringBuilder();
        StringBuilder sb_time = new StringBuilder();
        StringBuilder sb_power = new StringBuilder();
        StringBuilder sb_ftp = new StringBuilder();
        StringBuilder sb_hr = new StringBuilder();
        //sb_distance.append("Distance(mi)");
        sb_time.append("Time");
        sb_power.append("Power");
        sb_ftp.append("FTP");
        sb_hr.append("HR");
        boolean first = true;
        String tab = "     ";
        for (StravaActivity act : athlete.getActivityList()) {
            if (act.getDate() <= dtMax && act.getDate() >= dtMillis) {
                if (first)
                    sb_distance.append("Distance");
                String val = tab + sdf.format(act.getDate()) + tab + mToMi(act.getDistance()) + tab + sToTime(act.getMovingTime()) +
                        "\n" + (act.getPwrAvg() > 0 ? tab + "AvgP: " + act.getPwrAvg() : "") +
                        (act.getPwrFtp() > 0 ? tab + "MaxFtp: " + act.getPwrFtp() : "") +
                        (act.getHrAvg() > 0 ? tab + "AvgHr: " + act.getHrAvg() : "");
                String[] stringValues = new String[5];
                int[] intValues = new int[5];
                stringValues[0] = "\n" + mToMi(act.getDistance());
                intValues[0] = stringValues[0].length();
                stringValues[1] = "\n" + sToTime(act.getMovingTime());
                intValues[1] = stringValues[1].length();
                stringValues[2] = "\n" + act.getPwrAvg();
                intValues[2] = stringValues[2].length();
                stringValues[3] = "\n" + act.getPwrFtp();
                intValues[3] = stringValues[3].length();
                stringValues[4] = "\n" + act.getHrAvg();
                intValues[4] = stringValues[4].length();
                String val2 = (first ? tab + "Distance" + tab + "Time" + tab + "Power" + tab + "FTP" + tab + "HR" + "\n" : "") +
                        tab + stringValues[0] + getSpace(5 + 8 - intValues[0]) +
                        stringValues[1] + getSpace(5 + 4 - intValues[1]) +
                        stringValues[2] + getSpace(5 + 5 - intValues[2]) +
                        stringValues[3] + getSpace(5 + 3 - intValues[3]) +
                        stringValues[4];

                sb_distance.append(stringValues[0]);
                sb_time.append(stringValues[1]);
                sb_power.append(stringValues[2]);
                sb_ftp.append(stringValues[3]);
                sb_hr.append(stringValues[4]);

                //sb.append(val2);
                first = false;
            }
            if (act.getDate() > dtMax)
                break;
        }
        if (sb_distance.length() > 0 ) {
            activityDetails.setVisibility(View.VISIBLE);
            ((TextView) vItem.findViewById(R.id.tv_distance)).setText(sb_distance.toString());
            ((TextView) vItem.findViewById(R.id.tv_time)).setText(sb_time.toString());
            ((TextView) vItem.findViewById(R.id.tv_power)).setText(sb_power.toString());
            ((TextView) vItem.findViewById(R.id.tv_ftp)).setText(sb_ftp.toString());
            ((TextView) vItem.findViewById(R.id.tv_hr)).setText(sb_hr.toString());
        } else
            activityDetails.setVisibility(View.GONE);
    }

    private String getSpace(int c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= c; i++)
            sb.append(" ");
        return sb.toString();
    }

    private String getStringVal(double val) {
        int iVal = (int) (val*10);
        return String.valueOf((double) iVal/10d);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}