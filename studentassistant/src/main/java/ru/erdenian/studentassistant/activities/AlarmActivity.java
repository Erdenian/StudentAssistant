package ru.erdenian.studentassistant.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Locale;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.Utils.Utils;
import ru.erdenian.studentassistant.constants.SharedPreferencesConstants;
import ru.erdenian.studentassistant.services.AlarmService;

/**
 * Created by Erdenian on 13.08.2016.
 * Todo: описание класса
 */

public class AlarmActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    SwitchCompat scAlarm;
    EditText etHour, etMinute;
    Button btnSaveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        editor.apply();

        toolbar = (Toolbar) findViewById(R.id.ta_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.alarm);

        drawerLayout = Utils.initializeNavigationView(getResources(), toolbar, this);

        scAlarm = (SwitchCompat) findViewById(R.id.ca_alarm_switch);
        scAlarm.setChecked(sharedPreferences.getBoolean(SharedPreferencesConstants.ENABLE_ALARM, false));
        scAlarm.setOnCheckedChangeListener(this);

        etHour = (EditText) findViewById(R.id.ca_hour);
        etHour.setText(String.format(Locale.getDefault(), "%d",
                sharedPreferences.getInt(SharedPreferencesConstants.ALARM_HOUR, 1)));
        etHour.setEnabled(scAlarm.isChecked());
        etHour.addTextChangedListener(this);

        etMinute = (EditText) findViewById(R.id.ca_minute);
        etMinute.setText(String.format(Locale.getDefault(), "%d",
                sharedPreferences.getInt(SharedPreferencesConstants.ALARM_MINUTE, 0)));
        etMinute.setEnabled(scAlarm.isChecked());
        etMinute.addTextChangedListener(this);

        btnSaveTime = (Button) findViewById(R.id.ca_save_time);
        btnSaveTime.setEnabled(scAlarm.isChecked());
        btnSaveTime.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        editor.putBoolean(SharedPreferencesConstants.ENABLE_ALARM, b);
        editor.commit();

        etHour.setEnabled(b);
        etMinute.setEnabled(b);
        btnSaveTime.setEnabled(b);

        Intent alarmService = new Intent(this, AlarmService.class);

        if (b)
            startService(alarmService);
        else
            stopService(alarmService);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if ((etHour.length() == 0) || (etMinute.length() == 0))
            btnSaveTime.setEnabled(false);
        else
            btnSaveTime.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ca_alarm_switch:
                editor.putInt(SharedPreferencesConstants.ALARM_HOUR,
                        Integer.valueOf(etHour.getText().toString()));
                editor.putInt(SharedPreferencesConstants.ALARM_MINUTE,
                        Integer.valueOf(etMinute.getText().toString()));
                editor.apply();

                btnSaveTime.setEnabled(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}
