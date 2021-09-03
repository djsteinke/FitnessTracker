package com.rn5.fitnesstracker.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class DatePickerAlert {
    private static final String TAG = DatePickerAlert.class.getSimpleName();
    private final Context context;
    private final DatePickerDialog.OnDateSetListener listener;
    private Date date;

    public DatePickerAlert(Context context, DatePickerDialog.OnDateSetListener listener) {
        Log.d(TAG, "DatePickerAlert()");
        this.context = context;
        this.listener = listener;
    }

    public DatePickerAlert withDate(Date date) {
        this.date = date;
        return this;
    }

    public void show() {
        final Calendar c = Calendar.getInstance();
        if (date != null) {
            c.setTime(date);
        }
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener, mYear, mMonth, mDay);
        datePickerDialog.show();
        /*


         */
    }
}
