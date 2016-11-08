package ru.erdenian.studentassistant.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import org.joda.time.LocalDate;

import java.util.Calendar;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter;
import ru.erdenian.studentassistant.schedule.ScheduleManager;
import ru.erdenian.studentassistant.schedule.Semester;
import ru.erdenian.studentassistant.ulils.UiUtils;

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class ScheduleActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private Semester semester = ScheduleManager.getCurrentSemester();

    private DrawerLayout drawer;
    private LinearLayout llAddButtons;
    private ViewPager viewPager;
    private SchedulePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_schedule);
        setSupportActionBar(toolbar);

        drawer = UiUtils.initializeDrawerAndNavigationView(this, R.id.activity_schedule_drawer,
                toolbar, getResources());

        llAddButtons = (LinearLayout) findViewById(R.id.content_schedule_add_buttons);

        viewPager = (ViewPager) findViewById(R.id.content_schedule_view_pager);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.content_schedule_pager_tab_strip);
        pagerTabStrip.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ScheduleManager.getSemesters().isEmpty()) {
            viewPager.setVisibility(View.GONE);
        } else {
            pagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), semester);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(pagerAdapter.getPosition(LocalDate.now()));

            llAddButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        UiUtils.colorMenu(this, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_schedule_calendar:
                Calendar firstDay = Calendar.getInstance();
                firstDay.setTime(semester.getFirstDay().toDate());
                Calendar lastDay = Calendar.getInstance();
                lastDay.setTime(semester.getLastDay().toDate());

                MonthAdapter.CalendarDay startDate = new MonthAdapter.CalendarDay(firstDay);
                MonthAdapter.CalendarDay endDate = new MonthAdapter.CalendarDay(lastDay);

                new CalendarDatePickerDialogFragment()
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDateRange(startDate, endDate)
                        .setThemeCustom(R.style.DatePicker)
                        .setOnDateSetListener(this)
                        .show(getSupportFragmentManager(), "date_picker");
                break;
            case R.id.menu_schedule_edit_schedule:
                Toast.makeText(this, R.string.menu_schedule_edit_schedule, Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + item.getItemId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        viewPager.setCurrentItem(pagerAdapter.getPosition(new LocalDate(year, monthOfYear + 1, dayOfMonth)));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
