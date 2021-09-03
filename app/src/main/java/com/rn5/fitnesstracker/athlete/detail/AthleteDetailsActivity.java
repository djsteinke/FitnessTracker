package com.rn5.fitnesstracker.athlete.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rn5.fitnesstracker.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.rn5.fitnesstracker.MainActivity.athlete;
import static com.rn5.fitnesstracker.MainActivity.bDarkMode;

public class AthleteDetailsActivity extends AppCompatActivity implements AthleteDetailsListener {

    private final String TAG = getClass().getSimpleName();
    private AthleteDetailsListAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator((bDarkMode?R.drawable.ic_arrow_back_white_24dp:R.drawable.ic_arrow_back_black_24dp));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.athlete_details);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rvAdapter = new AthleteDetailsListAdapter(this, athlete.getDetailList());
        recyclerView.setAdapter(rvAdapter);

        FloatingActionButton fab = findViewById(R.id.addAthleteDetails);
        fab.setOnClickListener(view -> addAthleteDetails());
    }

    @Override
    public void onAthleteDetailsUpdate() {
        Log.d(TAG, "onAthleteDetailsUpdate()");
        rvAdapter.notifyDataSetChanged();
    }

    private void addAthleteDetails() {
        Log.d(TAG, "addAthleteDetails()");
        AthleteDetailsAlert alert = new AthleteDetailsAlert(this, this);
        alert.show();
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
