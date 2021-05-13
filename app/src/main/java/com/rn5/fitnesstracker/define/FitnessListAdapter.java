package com.rn5.fitnesstracker.define;

import android.content.Context;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rn5.fitnesstracker.R;
import com.rn5.fitnesstracker.model.Fitness;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.rn5.fitnesstracker.activity.MainActivity.bDarkMode;
import static com.rn5.fitnesstracker.activity.MainActivity.dayInMS;

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