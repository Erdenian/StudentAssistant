package ru.erdenian.studentassistant.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import ru.erdenian.studentassistant.constants.SharedPreferencesConstants;

/**
 * Created by Erdenian on 23.07.2016.
 * Todo: описание класса
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        String id = sPref.getString(SharedPreferencesConstants.GROUP_ID, "");
        if (!id.equals(""))
            startActivity(new Intent(this, ScheduleActivity.class));
        else
            startActivity(new Intent(this, UniversitySelectionActivity.class));
        finish();
    }
}