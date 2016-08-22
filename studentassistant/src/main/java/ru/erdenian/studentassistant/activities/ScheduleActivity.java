package ru.erdenian.studentassistant.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import org.joda.time.LocalDate;

import java.util.Calendar;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.Utils.Utils;
import ru.erdenian.studentassistant.adapters.SchedulePagerAdapter;
import ru.erdenian.studentassistant.adapters.SemestersSpinnerAdapter;
import ru.erdenian.studentassistant.classes.Semester;
import ru.erdenian.studentassistant.helpers.SemestersHelper;

/**
 * Created by Erdenian on 23.07.2016.
 * Todo: описание класса
 */

public class ScheduleActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        View.OnClickListener {

    final String CURRENT_PAGE = "current_page",
            DATE_PICKER_TAG = "tag_date_picker";

    SharedPreferences sharedPreferences;
    int savedPage = -1;

    Semester selectedSemester;

    Toolbar toolbar;
    Spinner spSemesters;
    DrawerLayout drawerLayout;
    Button btnAddSchedule;
    ViewPager viewPager;
    SchedulePagerAdapter pagerAdapter;
    PagerTabStrip pagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.ts_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        spSemesters = (Spinner) findViewById(R.id.ts_spinner);
        spSemesters.setOnItemSelectedListener(this);

        drawerLayout = Utils.initializeNavigationView(getResources(), toolbar, this);

        btnAddSchedule = (Button) findViewById(R.id.cs_add_schedule);
        btnAddSchedule.setOnClickListener(this);

        pagerTabStrip = (PagerTabStrip) findViewById(R.id.cs_pager_tab_strip);
        pagerTabStrip.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);

        viewPager = (ViewPager) findViewById(R.id.cs_view_pager);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putInt(CURRENT_PAGE, viewPager.getCurrentItem());
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        savedPage = bundle.getInt(CURRENT_PAGE, -1);
        super.onRestoreInstanceState(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SchedulePagerAdapter.setShowWeekNumbers(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.show_week_numbers_key), false));

        if (SemestersHelper.hasSemesters()) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayShowTitleEnabled(false);

            SemestersSpinnerAdapter spinnerAdapter = new SemestersSpinnerAdapter(getLayoutInflater());
            spSemesters.setAdapter(spinnerAdapter);
            spSemesters.setSelection(spinnerAdapter.getDefaultPosition(), false);

            btnAddSchedule.setVisibility(View.GONE);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(R.string.no_schedule);
            }
            spSemesters.setVisibility(View.GONE);
            pagerTabStrip.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        menu.findItem(R.id.ms_calendar).setVisible(selectedSemester != null);
        menu.findItem(R.id.ms_schedule_editor).setVisible(SemestersHelper.hasSemesters());
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (SemestersHelper.hasSemesters()) {
            selectedSemester = (Semester) parent.getItemAtPosition(position);
            pagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), selectedSemester);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(savedPage != -1 ? savedPage : pagerAdapter.START_PAGE, false);
            savedPage = -1;
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ms_calendar:
                LocalDate preselected = pagerAdapter.getDate(viewPager.getCurrentItem()),
                        firstDay = selectedSemester.getFirstDay(),
                        lastDay = selectedSemester.getLastDay();

                MonthAdapter.CalendarDay first = new MonthAdapter.CalendarDay(firstDay.getYear(),
                        firstDay.getMonthOfYear() - 1, firstDay.getDayOfMonth());
                MonthAdapter.CalendarDay last = new MonthAdapter.CalendarDay(lastDay.getYear(),
                        lastDay.getMonthOfYear() - 1, lastDay.getDayOfMonth());

                new CalendarDatePickerDialogFragment()
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDateRange(first, last)
                        .setPreselectedDate(preselected.getYear(),
                                preselected.getMonthOfYear() - 1,
                                preselected.getDayOfMonth())
                        .setThemeCustom(R.style.DatePicker)
                        .setOnDateSetListener(this)
                        .show(getSupportFragmentManager(), DATE_PICKER_TAG);
                break;
            case R.id.ms_schedule_editor:
                startActivity(new Intent(this, ScheduleEditorActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog,
                          int year, int monthOfYear, int dayOfMonth) {
        LocalDate newDate = new LocalDate(year, monthOfYear + 1, dayOfMonth);
        viewPager.setCurrentItem(pagerAdapter.getPosition(newDate));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cs_add_schedule:
                startActivity(new Intent(this, ScheduleEditorActivity.class));
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
