package ru.erdenian.studentassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.erdenian.studentassistant.Utils.FileUtils;

/**
 * Created by Erdenian on 23.07.2016.
 * Todo: описание класса
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileUtils.initialize(this);

        startActivity(new Intent(this, ScheduleActivity.class));
        finish();
    }
}