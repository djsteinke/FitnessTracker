package rnfive.htfu.fitnesstracker.athlete.fitness;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import rnfive.htfu.fitnesstracker.R;
import rnfive.htfu.fitnesstracker.strava.StravaActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static rnfive.htfu.fitnesstracker.MainActivity.athlete;
import static rnfive.htfu.fitnesstracker.MainActivity.bDarkMode;
import static rnfive.htfu.fitnesstracker.util.Constants.sToTime;

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
        LinearLayout llActivities = vItem.findViewById(R.id.ll_activities);

        /*
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        Calendar c = Calendar.getInstance();
        long dtMillis = mDataset.get(position).getDate()*dayInMS - c.getTimeZone().getRawOffset();
        c.setTimeInMillis(dtMillis);

         */
        //Log.d(TAG,"Millis [" + dtMillis + "] Date [" + sdf.format(c.getTime()) +"]" + "rawOffset [" + c.getTimeZone().getRawOffset() + "]");

        long epochDay = mDataset.get(position).getId();

        LocalDate ld = LocalDate.ofEpochDay(epochDay);

        //date.setText(sdf.format(c.getTime()));
        date.setText(ld.format(DateTimeFormatter.ofPattern("EEE, MMM dd")));
        // TODO
        //pss.setText(String.valueOf(mDataset.get(position).getPwrStressScore()));
        //hrss.setText(String.valueOf(mDataset.get(position).getHrStressScore()));
        fitness.setText(getStringVal(mDataset.get(position).getFitness()));
        fatigue.setText(getStringVal(mDataset.get(position).getFatigue()));
        form.setText(getStringVal(mDataset.get(position).getForm()));
        //long dtMax = dtMillis + dayInMS;
        long dtMax = epochDay + 1;
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
        boolean actExists = false;
        LinearLayout llType = llActivities.findViewById(R.id.ll_type);
        removeChildViews(llType);
        LinearLayout llTime = llActivities.findViewById(R.id.ll_time);
        removeChildViews(llTime);
        LinearLayout llPower = llActivities.findViewById(R.id.ll_power);
        removeChildViews(llPower);
        LinearLayout llHR = llActivities.findViewById(R.id.ll_hr);
        removeChildViews(llHR);
        LinearLayout llPss = llActivities.findViewById(R.id.ll_pss);
        removeChildViews(llPss);
        LinearLayout llHrss = llActivities.findViewById(R.id.ll_hrss);
        removeChildViews(llHrss);
        for (StravaActivity act : athlete.getActivityList()) {
            if (act.getDate() == epochDay) {
                actExists = true;
                String actType = act.getActivityType();
                if (actType.contains("Ride"))
                    actType = "Ride";
                else
                    actType = "Run";
                llType.addView(getTextView((TextView) llType.getChildAt(0), new SpannableStringBuilder(actType)));
                llTime.addView(getTextView((TextView) llTime.getChildAt(0), new SpannableStringBuilder(sToTime(act.getMovingTime()))));
                if (act.getPwrAvg() > 0) {
                    //SpannableStringBuilder ssb = new SpannableStringBuilder("Pwr: " + act.getPwrAvg());
                    //ssb.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder ssb = new SpannableStringBuilder(act.getPwrAvg() + "w");
                    ssb.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), ssb.length()-1, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    llPower.addView(getTextView((TextView) llPower.getChildAt(0), ssb));
                } else
                    llPower.addView(getTextView((TextView) llPower.getChildAt(0), new SpannableStringBuilder(" ")));
                if (act.getHrAvg() > 0) {
                    //SpannableStringBuilder ssb = new SpannableStringBuilder("HR: " + act.getHrAvg());
                    //ssb.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder ssb = new SpannableStringBuilder(act.getHrAvg() + "bpm");
                    ssb.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), ssb.length()-3, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    llHR.addView(getTextView((TextView) llHR.getChildAt(0), ssb));
                } else
                    llHR.addView(getTextView((TextView) llHR.getChildAt(0), new SpannableStringBuilder(" ")));
                int pssColor = (act.getPss()>=act.getHrss()?R.color.red_light:R.color.red);
                int hrssColor = (act.getPss()<act.getHrss()?R.color.red_light:R.color.red);
                if (act.getPss() > 0) {
                    SpannableStringBuilder ssb = new SpannableStringBuilder("PSS: " + act.getPss());
                    ssb.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    ssb.setSpan(new ForegroundColorSpan(context.getColor(pssColor)), 4, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    llPss.addView(getTextView((TextView) llPss.getChildAt(0), ssb));
                } else
                    llPss.addView(getTextView((TextView) llPss.getChildAt(0), new SpannableStringBuilder(" ")));
                if (act.getHrAvg() > 0) {
                    SpannableStringBuilder ssb = new SpannableStringBuilder("HRSS: " + act.getHrss());
                    ssb.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    ssb.setSpan(new ForegroundColorSpan(context.getColor(hrssColor)), 5, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    llHrss.addView(getTextView((TextView) llHrss.getChildAt(0), ssb));
                } else
                    llHrss.addView(getTextView((TextView) llHrss.getChildAt(0), new SpannableStringBuilder(" ")));
            }
        }
        llActivities.setVisibility((actExists?View.VISIBLE:View.GONE));
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

    private TextView getTextView(TextView orig, SpannableStringBuilder val) {
        TextView newView = new TextView(orig.getContext());
        newView.setLayoutParams(orig.getLayoutParams());
        newView.setText(val);
        return newView;
    }

    private void removeChildViews(LinearLayout ll) {
        if (ll.getChildCount() > 1)
            ll.removeViews(1, (ll.getChildCount()-1));
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