package ru.erdenian.studentassistant.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TimePicker;

import ru.erdenian.studentassistant.R;

public class AlarmActivity extends AppCompatActivity {

    TimePicker tpTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tpTime = (TimePicker) findViewById(R.id.aa_time_picker);
    }
}
