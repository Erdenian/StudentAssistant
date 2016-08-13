package ru.erdenian.studentassistant.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TimePicker;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.classes.Utils;

/**
 * Created by Erdenian on 13.08.2016.
 */

public class AlarmActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    TimePicker tpTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        toolbar = (Toolbar) findViewById(R.id.ta_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.alarm);

        drawerLayout = Utils.initializeNavigationView(getResources(), toolbar, this);

        tpTime = (TimePicker) findViewById(R.id.ca_time_picker);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}
