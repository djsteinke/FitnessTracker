package rnfive.htfu.fitnesstracker.athlete.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import rnfive.htfu.fitnesstracker.R;
import rnfive.htfu.fitnesstracker.util.DatePickerAlert;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static rnfive.htfu.fitnesstracker.MainActivity.athlete;
import static rnfive.htfu.fitnesstracker.MainActivity.getDaysTo;
import static rnfive.htfu.fitnesstracker.util.Constants.sdfDate;
import static rnfive.htfu.fitnesstracker.util.Constants.sdfDatePattern;

public class AthleteDetailsAlert implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = AthleteDetailsAlert.class.getSimpleName();
    private final Context context;
    private final AthleteDetailsListener listener;
    private AthleteDetail athleteDetail;
    private EditText etDate;

    public AthleteDetailsAlert(Context context, AthleteDetailsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public AthleteDetailsAlert withAthleteDetails(int pos) {
        this.athleteDetail = athlete.getDetailList().get(pos);
        return this;
    }

    public void delete() {
        if (athleteDetail != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Delete details?");
            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            })
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                        athlete.removeAthleteDetail(athleteDetail);
                        athlete.save();
                        listener.onAthleteDetailsUpdate();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void show() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        ViewGroup viewGroup = ((Activity) context).findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_athlete_details, viewGroup, false);

        final EditText etFtp = dialogView.findViewById(R.id.et_ftp);
        final EditText etHrm = dialogView.findViewById(R.id.et_hrm);
        final EditText etHrr = dialogView.findViewById(R.id.et_hrr);
        etDate = dialogView.findViewById(R.id.et_date);
        CheckBox auto = dialogView.findViewById(R.id.cb_auto);

        if (athleteDetail != null) {
            etFtp.setText(String.valueOf(athleteDetail.getFtp()));
            etHrm.setText(String.valueOf(athleteDetail.getHrm()));
            etHrr.setText(String.valueOf(athleteDetail.getHrr()));
            LocalDate ld = LocalDate.ofEpochDay(athleteDetail.getId());
            etDate.setText(ld.format(DateTimeFormatter.ofPattern(sdfDatePattern)));
            auto.setChecked(athleteDetail.isAuto());
        }

        final DatePickerDialog.OnDateSetListener dateSetListener = this;

        final ImageButton datePicker = dialogView.findViewById(R.id.date_picker_button);

        datePicker.setOnClickListener(view -> {
            DatePickerAlert alert = new DatePickerAlert(context, dateSetListener);
            alert.show();
        });

        builder.setView(dialogView)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                })
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {

                    Log.d(TAG,"addAthleteDetails() - OK pressed");
                    if (!etFtp.getText().toString().isEmpty() &&
                            !etHrm.getText().toString().isEmpty() &&
                            !etHrr.getText().toString().isEmpty() &&
                            !etDate.getText().toString().isEmpty()) {


                        Date date = null;
                        try {
                            date = sdfDate.parse(etDate.getText().toString());
                        } catch (ParseException e) {
                            Log.e(TAG,"addAthleteDetails() : date parse exception : " + e.getMessage());
                        }
                        Calendar cal = Calendar.getInstance();
                        if (date != null) {
                            cal.setTime(date);
                            cal.add(Calendar.SECOND,1);
                            if (athleteDetail != null) {
                                athleteDetail.setFtp(Integer.parseInt(etFtp.getText().toString()));
                                athleteDetail.setHrm(Integer.parseInt(etHrm.getText().toString()));
                                athleteDetail.setHrr(Integer.parseInt(etHrr.getText().toString()));
                                athleteDetail.setAuto(auto.isChecked());
                                athlete.updateAthleteDetail(athleteDetail);
                            } else {
                                AthleteDetail detail = new AthleteDetail(
                                        Integer.parseInt(etFtp.getText().toString()),
                                        Integer.parseInt(etHrm.getText().toString()),
                                        Integer.parseInt(etHrr.getText().toString()),
                                        getDaysTo(cal)).isAuto(auto.isChecked());
                                athlete.addAthleteDetail(detail);
                            }
                            athlete.save();
                            listener.onAthleteDetailsUpdate();
                            //rvAdapter.notifyDataSetChanged();
                            Log.d(TAG, "addAthleteDetails() - details Saved");
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year,
                          int monthOfYear, int dayOfMonth) {

        //date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        int month = monthOfYear + 1;
        String date = year + "-" + ((month<10?"0"+month:month)) + "-" + ((dayOfMonth<10?"0"+dayOfMonth:dayOfMonth));
        if (etDate != null) {
            etDate.setText(date);
        }
    }
}
