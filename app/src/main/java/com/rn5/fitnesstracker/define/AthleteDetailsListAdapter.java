package com.rn5.fitnesstracker.define;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rn5.fitnesstracker.R;
import com.rn5.fitnesstracker.model.AthleteDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.rn5.fitnesstracker.activity.MainActivity.athleteData;
import static com.rn5.fitnesstracker.activity.MainActivity.dayInMS;
import static com.rn5.fitnesstracker.define.Constants.sdfDate;

public class AthleteDetailsListAdapter extends RecyclerView.Adapter<AthleteDetailsListAdapter.MyViewHolder> {
    private static final String TAG = AthleteDetailsListAdapter.class.getSimpleName();
    private static List<AthleteDetail> mDataset = new ArrayList<>();


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
    public AthleteDetailsListAdapter(List<AthleteDetail> inDataset) {
        mDataset = inDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public AthleteDetailsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.athlete_details, parent, false);
        return new AthleteDetailsListAdapter.MyViewHolder(v);
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AthleteDetailsListAdapter.MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d(TAG,"onBindViewHolder() [" + position + "]");
        final View vItem = holder.vItem;
        vItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                athleteData.removeAthleteDetail(position);
                athleteData.save();
                notifyDataSetChanged();
                return false;
            }
        });
        TextView date = vItem.findViewById(R.id.date_value);
        TextView ftp = vItem.findViewById(R.id.ftp_value);
        TextView hrMin = vItem.findViewById(R.id.hr_min_value);
        TextView hrMax = vItem.findViewById(R.id.hr_max_value);

        Calendar c = Calendar.getInstance();
        long dtMillis = mDataset.get(position).getDate() * dayInMS - c.getTimeZone().getRawOffset();
        c.setTimeInMillis(dtMillis);
        //Log.d(TAG,"Millis [" + dtMillis + "] Date [" + sdf.format(c.getTime()) +"]" + "rawOffset [" + c.getTimeZone().getRawOffset() + "]");

        date.setText(sdfDate.format(c.getTime()));
        ftp.setText(String.valueOf(mDataset.get(position).getFtp()));
        hrMin.setText(String.valueOf(mDataset.get(position).getHrr()));
        hrMax.setText(String.valueOf(mDataset.get(position).getHrm()));
    }

    private String getStringVal(double val) {
        int iVal = (int) (val * 10);
        return String.valueOf((double) iVal / 10d);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}