package com.rn5.fitnesstracker.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rn5.fitnesstracker.R;
import com.rn5.fitnesstracker.define.AthleteDetailsListAdapter;
import com.rn5.fitnesstracker.model.AthleteDetail;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.rn5.fitnesstracker.activity.MainActivity.athleteData;
import static com.rn5.fitnesstracker.activity.MainActivity.bDarkMode;
import static com.rn5.fitnesstracker.define.Constants.sdfDate;
import static com.rn5.fitnesstracker.activity.MainActivity.getDaysTo;

public class AthleteDetailsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private EditText ftp;
    private EditText hrm;
    private EditText hrr;

    private RecyclerView.Adapter rvAdapter;

    private Context context;

    private String date;
    private EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_details);

        context = this;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator((bDarkMode?R.drawable.ic_arrow_back_white_24dp:R.drawable.ic_arrow_back_black_24dp));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.athlete_details);
        }

        ftp = findViewById(R.id.et_ftp);
        hrm = findViewById(R.id.et_hrm);
        hrr = findViewById(R.id.et_hrr);

        AthleteDetail athleteDetail = athleteData.getDetailList().size()>0?athleteData.getDetailList().get(0):null;
        if (athleteDetail != null) {
            ftp.setText(String.valueOf(athleteDetail.getFtp()));
            hrm.setText(String.valueOf(athleteDetail.getHrm()));
            hrr.setText(String.valueOf(athleteDetail.getHrr()));
        } else {
            ftp.setText("0");
            hrm.setText("0");
            hrr.setText("0");
        }

        ftp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT) {
                    AthleteDetail newDetail = new AthleteDetail(Integer.parseInt(ftp.getText().toString()),
                            Integer.parseInt(hrm.getText().toString()),
                            Integer.parseInt(hrr.getText().toString()),
                            Calendar.getInstance().getTimeInMillis());
                    athleteData.getDetailList().add(0,newDetail);
                    athleteData.save();
                    return i == EditorInfo.IME_ACTION_DONE;
                }
                return false;
            }
        });

        hrm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT) {
                    //return i == EditorInfo.IME_ACTION_DONE;
                }
                return false;
            }
        });

        hrr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT) {
                    //return i == EditorInfo.IME_ACTION_DONE;
                }
                return false;
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rvAdapter = new AthleteDetailsListAdapter(athleteData.getDetailList());
        recyclerView.setAdapter(rvAdapter);

        final Context context = this;
        FloatingActionButton fab = findViewById(R.id.addAthleteDetails);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAthleteDetails();
            }
        });
    }

    private void addAthleteDetails() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        ViewGroup viewGroup = ((Activity) context).findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_athlete_details, viewGroup, false);


        final EditText etFtp = dialogView.findViewById(R.id.et_ftp);
        final EditText etHrm = dialogView.findViewById(R.id.et_hrm);
        final EditText etHrr = dialogView.findViewById(R.id.et_hrr);
        etDate = dialogView.findViewById(R.id.et_date);

        final ImageButton datePicker = dialogView.findViewById(R.id.date_picker_button);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        builder.setView(dialogView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                                AthleteDetail detail = new AthleteDetail(
                                        Integer.parseInt(etFtp.getText().toString()),
                                        Integer.parseInt(etHrm.getText().toString()),
                                        Integer.parseInt(etHrr.getText().toString()),
                                        getDaysTo(cal));
                                athleteData.addAthleteDetail(detail);
                                athleteData.save();
                                rvAdapter.notifyDataSetChanged();
                                Log.d(TAG, "addAthleteDetails() - details Saved");
                            }
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        int month = monthOfYear + 1;
                        date = year + "-" + ((month<10?"0"+month:month)) + "-" + ((dayOfMonth<10?"0"+dayOfMonth:dayOfMonth));

                        if (etDate != null) {
                            etDate.setText(date);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}
