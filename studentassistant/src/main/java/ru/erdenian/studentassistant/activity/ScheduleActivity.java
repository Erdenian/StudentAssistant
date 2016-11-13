package ru.erdenian.studentassistant.activity;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.adapter.SchedulePagerAdapter;
import ru.erdenian.studentassistant.schedule.Homework;
import ru.erdenian.studentassistant.schedule.Lesson;
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener;
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
public class ScheduleActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        View.OnClickListener,
        OnScheduleUpdateListener {

    private static final String CURRENT_PAGE = "current_page";

    private int savedPage = -1;

    private Semester selectedSemester;

    private DrawerLayout drawer;
    private Spinner spSemesters;
    private LinearLayout llAddButtons;
    private ViewPager viewPager;
    private SchedulePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_with_spinner);
        setSupportActionBar(toolbar);

        drawer = UiUtils.initializeDrawerAndNavigationView(this, R.id.activity_schedule_drawer,
                toolbar, getResources());

        spSemesters = (Spinner) findViewById(R.id.toolbar_with_spinner_spinner);
        spSemesters.setOnItemSelectedListener(this);

        llAddButtons = (LinearLayout) findViewById(R.id.content_schedule_add_buttons);
        Button btnGetScheduleFromServer = (Button) findViewById(R.id.content_schedule_get_schedule_from_server);
        btnGetScheduleFromServer.setOnClickListener(this);
        Button btnAddSchedule = (Button) findViewById(R.id.content_schedule_add_schedule);
        btnAddSchedule.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.view_pager_pager_tab_strip);
        pagerTabStrip.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScheduleManager.setOnScheduleUpdateListener(this);
        onScheduleUpdate();
    }

    @Override
    public void onScheduleUpdate() {
        getSupportActionBar().setDisplayShowTitleEnabled(ScheduleManager.getSemesters().size() <= 1);
        spSemesters.setVisibility((ScheduleManager.getSemesters().size() > 1) ? View.VISIBLE : View.GONE);

        viewPager.setVisibility((ScheduleManager.getSemesters().size() > 0) ? View.VISIBLE : View.GONE);
        llAddButtons.setVisibility((ScheduleManager.getSemesters().size() == 0) ? View.VISIBLE : View.GONE);

        if ((pagerAdapter != null) && (selectedSemester.getId() == ScheduleManager.getSelectedSemester().getId())) {
            savedPage = viewPager.getCurrentItem();
        }
        selectedSemester = ScheduleManager.getSelectedSemester();

        if (ScheduleManager.getSemesters().size() > 1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_semesters, ScheduleManager.getSemestersNames());
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_semesters);
            spSemesters.setAdapter(adapter);
            spSemesters.setSelection(ScheduleManager.getSelectedSemesterIndex());
        } else if (ScheduleManager.getSemesters().size() == 1) {
            getSupportActionBar().setTitle(selectedSemester.getName());
            onItemSelected(null, null, 0, 0);
        } else {
            getSupportActionBar().setTitle(R.string.title_activity_schedule);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        menu.findItem(R.id.menu_schedule_calendar).setVisible(!ScheduleManager.getSemesters().isEmpty());
        UiUtils.colorMenu(this, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ScheduleManager.setSelectedSemesterIndex(i);
        pagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), selectedSemester);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem((savedPage != -1) ? savedPage : pagerAdapter.getPosition(LocalDate.now()), false);
        savedPage = -1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_schedule_calendar:
                UiUtils.showDatePicker(selectedSemester.getFirstDay(), selectedSemester.getLastDay(), LocalDate.now(),
                        getSupportFragmentManager(), this);
                break;
            case R.id.menu_schedule_edit_schedule:
                startActivity(new Intent(this, SemestersEditorActivity.class));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.content_schedule_get_schedule_from_server:
                Toast.makeText(this, R.string.activity_schedule_get_schedule_from_server_button, Toast.LENGTH_SHORT).show();
                break;
            case R.id.content_schedule_add_schedule:

                ImmutableSortedSet<Lesson> lessons = ImmutableSortedSet.of(
                        new Lesson("Конструирование ПО", "Лабораторная работа",
                                ImmutableSortedSet.of("Федоров Алексей Роальдович", "Федоров Петр Алексеевич"), ImmutableSortedSet.of("4212а"),
                                new LocalTime(14, 20), new LocalTime(15, 50),
                                Lesson.RepeatType.BY_WEEKDAY, 5, ImmutableList.of(false, true), null),
                        new Lesson("Конструирование ПО", "Лабораторная работа",
                                ImmutableSortedSet.of("Федоров Алексей Роальдович"), ImmutableSortedSet.of("4212а"),
                                new LocalTime(18, 10), new LocalTime(19, 40),
                                Lesson.RepeatType.BY_WEEKDAY, 5, ImmutableList.of(false, true), null));

                ImmutableSortedSet<Semester> semesters = ImmutableSortedSet.of(
                        new Semester("Семестр 5", new LocalDate(2016, 9, 1), new LocalDate(2016, 12, 31),
                                lessons, ImmutableSortedSet.<Homework>of()));

                ScheduleManager.setSemesters(semesters);

                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + view.getId());
                break;
        }
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
