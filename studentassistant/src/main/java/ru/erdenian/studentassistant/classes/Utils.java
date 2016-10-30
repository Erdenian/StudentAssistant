package ru.erdenian.studentassistant.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.activities.AlarmActivity;
import ru.erdenian.studentassistant.activities.ScheduleActivity;
import ru.erdenian.studentassistant.activities.SettingsActivity;
import ru.erdenian.studentassistant.activities.UniversitySelectionActivity;

/**
 * Created by Erdenian on 18.07.2016.
 * Todo: описание класса
 */

public final class Utils {

    /**
     * Преобразует dp в px
     *
     * @param context контекст
     * @param dp      значение в dp
     * @return значение в px
     */
    public static int dpToPx(Context context, int dp) {
        // Todo: проверить правильность преобразования
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Преобразует px в dp
     *
     * @param context контекст
     * @param px      значение в px
     * @return значение в dp
     */
    public static int pxToDp(Context context, int px) {
        return Math.round(px / (context.getResources().getDisplayMetrics().xdpi /
                DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Поиск по списку
     *
     * @param list  список
     * @param query запрос
     * @param <T>   тип списка
     * @return список того же типа с найденными элементами
     */
    public static <T> ArrayList<T> search(ArrayList<T> list, String query) {
        // Todo: нормальный поиск
        if ((query == null) || (query.length() == 0))
            return list;

        ArrayList<T> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++)
            if (list.get(i).toString().toLowerCase().contains(query.toLowerCase()))
                result.add(list.get(i));

        return result;
    }

    public static DrawerLayout initializeNavigationView(Resources resources,
                                                        Toolbar toolbar, final Activity currentActivity) {
        View view = currentActivity.getWindow().getDecorView();

        DrawerLayout drawerLayout;
        LinearLayout llSchedule, llMap, llAlarm,
                llSettings, llHelp,
                llUniversitySelectionActivity, llClearData;

        drawerLayout = (DrawerLayout) view.findViewById(R.id.as_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(currentActivity, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        final DrawerLayout finalDrawerLayout = drawerLayout;
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nv_schedule:
                        if (!(currentActivity instanceof ScheduleActivity)) {
                            currentActivity.startActivity(new Intent(currentActivity,
                                    ScheduleActivity.class));
                            currentActivity.finish();
                        }
                        break;
                    case R.id.nv_map:

                        break;
                    case R.id.nv_alarm:
                        if (!(currentActivity instanceof AlarmActivity)) {
                            currentActivity.startActivity(new Intent(currentActivity,
                                    AlarmActivity.class));
                            currentActivity.finish();
                        }
                        break;
                    case R.id.nv_settings:
                        currentActivity.startActivity(new Intent(currentActivity,
                                SettingsActivity.class));
                        break;
                    case R.id.nv_help:

                        break;
                    case R.id.nv_university_selection_activity:
                        currentActivity.startActivity(new Intent(currentActivity,
                                UniversitySelectionActivity.class));
                        currentActivity.finish();
                        break;
                    case R.id.nv_clear_data:
                        try {
                            Runtime.getRuntime().exec("pm clear ru.erdenian.studentassistant");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                finalDrawerLayout.closeDrawer(GravityCompat.START);
            }
        };

        llSchedule = (LinearLayout) view.findViewById(R.id.nv_schedule);
        if (currentActivity instanceof ScheduleActivity) {
            llSchedule.setBackgroundColor(ContextCompat.getColor(currentActivity,
                    R.color.nav_selected_item_background));
            ((ImageView) view.findViewById(R.id.nv_schedule_icon))
                    .setColorFilter(ContextCompat.getColor(currentActivity, R.color.colorPrimary));
            ((TextView) view.findViewById(R.id.nv_schedule_text))
                    .setTextColor(ContextCompat.getColor(currentActivity, R.color.colorPrimary));
        }
        llSchedule.setOnClickListener(onClick);

        llMap = (LinearLayout) view.findViewById(R.id.nv_map);
        llMap.setOnClickListener(onClick);

        llAlarm = (LinearLayout) view.findViewById(R.id.nv_alarm);
        if (currentActivity instanceof AlarmActivity) {
            llAlarm.setBackgroundColor(ContextCompat.getColor(currentActivity,
                    R.color.nav_selected_item_background));
            ((ImageView) view.findViewById(R.id.nv_alarm_icon))
                    .setColorFilter(ContextCompat.getColor(currentActivity, R.color.colorPrimary));
            ((TextView) view.findViewById(R.id.nv_alarm_text))
                    .setTextColor(ContextCompat.getColor(currentActivity, R.color.colorPrimary));
        }
        llAlarm.setOnClickListener(onClick);

        llSettings = (LinearLayout) view.findViewById(R.id.nv_settings);
        llSettings.setOnClickListener(onClick);

        llHelp = (LinearLayout) view.findViewById(R.id.nv_help);
        llHelp.setOnClickListener(onClick);

        llUniversitySelectionActivity =
                (LinearLayout) view.findViewById(R.id.nv_university_selection_activity);
        llUniversitySelectionActivity.setOnClickListener(onClick);

        llClearData = (LinearLayout) view.findViewById(R.id.nv_clear_data);
        llClearData.setOnClickListener(onClick);

        return drawerLayout;
    }
}
