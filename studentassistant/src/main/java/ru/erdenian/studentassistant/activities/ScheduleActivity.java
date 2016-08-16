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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.adapters.SchedulePagerAdapter;
import ru.erdenian.studentassistant.adapters.SemestersSpinnerAdapter;
import ru.erdenian.studentassistant.classes.Lesson;
import ru.erdenian.studentassistant.classes.Semester;
import ru.erdenian.studentassistant.classes.Utils;
import ru.erdenian.studentassistant.constants.ServerConstants;
import ru.erdenian.studentassistant.constants.SharedPreferencesConstants;
import ru.erdenian.studentassistant.fragments.SchedulePageFragment;

/**
 * Created by Erdenian on 23.07.2016.
 * Todo: описание класса
 */

public class ScheduleActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        FutureCallback<File>,
        SwipeRefreshLayout.OnRefreshListener,
        ViewPager.OnPageChangeListener {

    final String CURRENT_PAGE = "current_page",
            DATE_PICKER_TAG = "tag_date_picker";

    File jsonFolder;
    String groupId;
    int savedPage = -1;

    ArrayList<Semester> semesters;
    Semester currentSemester, selectedSemester;
    ArrayList<Lesson> lessons;

    SharedPreferences sharedPreferences;

    Toolbar toolbar;
    Spinner spSemesters;
    DrawerLayout drawerLayout;
    LinearLayout llProgress;
    TextView tvProgressMessage;
    SwipeRefreshLayout swipeRefreshLayout;
    ViewPager viewPager;
    SchedulePagerAdapter pagerAdapter;
    PagerTabStrip pagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Todo: константа с путем к папке
        jsonFolder = new File(getFilesDir().getAbsolutePath() + "/json");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        groupId = sp.getString(SharedPreferencesConstants.GROUP_ID, "");
        if (groupId.equals("")) {
            startActivity(new Intent(this, UniversitySelectionActivity.class));
            finish();
            return;
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.ts_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = Utils.initializeNavigationView(getResources(), toolbar, this);

        llProgress = (LinearLayout) findViewById(R.id.pb_progress);
        tvProgressMessage = (TextView) findViewById(R.id.pb_progress_message);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        SchedulePageFragment.setSwipeRefreshLayout(swipeRefreshLayout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(this);
        SchedulePageFragment.setViewPager(viewPager);

        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);

        spSemesters = (Spinner) findViewById(R.id.ts_spinner);
        spSemesters.setOnItemSelectedListener(this);

        getSemesters(false);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        menu.findItem(R.id.ms_calendar).setVisible(selectedSemester != null);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSemester = (Semester) parent.getItemAtPosition(position);

        invalidateOptionsMenu();
        getLessons();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ms_calendar:
                // Todo: предвыбор текущей даты
                LocalDate preselected = currentSemester != null ?
                        new LocalDate() : pagerAdapter.getDate(viewPager.getCurrentItem()),
                        firstDay = selectedSemester.getFirstDay(),
                        lastDay = selectedSemester.getLastDay();
                MonthAdapter.CalendarDay first = new MonthAdapter.CalendarDay(firstDay.getYear(),
                        firstDay.getMonthOfYear() - 1, firstDay.getDayOfMonth());
                MonthAdapter.CalendarDay last = new MonthAdapter.CalendarDay(lastDay.getYear(),
                        lastDay.getMonthOfYear() - 1, lastDay.getDayOfMonth());
                new CalendarDatePickerDialogFragment()
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setPreselectedDate(preselected.getYear(),
                                preselected.getMonthOfYear() - 1,
                                preselected.getDayOfMonth())
                        .setDateRange(first, last)
                        .setThemeCustom(R.style.DatePicker)
                        .setOnDateSetListener(this)
                        .show(getSupportFragmentManager(), DATE_PICKER_TAG);
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
    public void onRefresh() {
        // Todo: нормальное обновление
        getSemesters(true);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
        SchedulePageFragment.setSwipeRefreshLayoutEnabled(state == ViewPager.SCROLL_STATE_IDLE);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    // Todo: возможно стоит полностью переделать получение json
    @Override
    public void onCompleted(Exception e, File file) {
        if (e != null) {
            // Todo: сообщение об ошибке
            return;
        }

        if (file.getName().equals("semesters.json")) {
            try {
                getSemesters(file);
            } catch (FileNotFoundException fnfeSemesters2) {
                fnfeSemesters2.printStackTrace();
                // Todo: сообщение об ошибке
            }
        } else {
            try {
                getLessons(file);
            } catch (FileNotFoundException fnfeLessons2) {
                fnfeLessons2.printStackTrace();
                // Todo: сообщение об ошибке
            }
        }
    }

    private void getSemesters(boolean deletePrevious) {
        if (deletePrevious) {
            File jsonFolder = new File(getFilesDir().getAbsolutePath() + "/json");
            File[] filesList = jsonFolder.listFiles();
            boolean deleted = true;

            if (filesList != null)
                for (File f : filesList)
                    deleted = f.delete() && deleted;

            if (deleted)
                deleted = jsonFolder.delete();

            //noinspection StatementWithEmptyBody
            if (!deleted) {
                // Todo: warning, что файлы не удалены
            }
        }

        File jsonFileSemesters = new File(jsonFolder.getAbsolutePath() + "/semesters.json");
        try {
            getSemesters(jsonFileSemesters);
        } catch (FileNotFoundException fnfeSemesters1) {
            viewPager.animate().alpha(0);
            llProgress.animate().alpha(1);
            tvProgressMessage.setText(R.string.getting_semesters_list);

            if (jsonFolder.mkdirs())
                Ion.with(this)
                        .load(ServerConstants.SERVER_URL +
                                ServerConstants.ROOT_FOLDER +
                                groupId +
                                "/semesters.json")
                        .write(jsonFileSemesters)
                        .setCallback(this);
        }
    }

    private void getSemesters(File jsonFile) throws FileNotFoundException {
        semesters = Converters.registerLocalDate(new GsonBuilder()).create().fromJson(
                new FileReader(jsonFile),
                new TypeToken<ArrayList<Semester>>() {
                }.getType());

        Collections.sort(semesters, new Comparator<Semester>() {
            @Override
            public int compare(Semester lhs, Semester rhs) {
                return Days.daysBetween(lhs.getFirstDay(), rhs.getFirstDay()).getDays();
            }
        });

        LocalDate today = new LocalDate();
        for (int i = 0; i < semesters.size(); i++) {
            Semester semester = semesters.get(i);
            if (!today.isBefore(semester.getFirstDay()) && !today.isAfter(semester.getLastDay()))
                currentSemester = semester;
        }

        if (currentSemester == null) {
            for (int i = 0; i < semesters.size(); i++) {
                if (today.isAfter(semesters.get(i).getLastDay())) {
                    semesters.add(i, null);
                    break;
                }
            }
            if (today.isBefore(semesters.get(semesters.size() - 1).getFirstDay()))
                semesters.add(null);
        }

        selectedSemester = currentSemester;

        spSemesters.setAdapter(new SemestersSpinnerAdapter(this, semesters));
        spSemesters.setSelection(semesters.indexOf(selectedSemester), false);
    }

    private void getLessons() {

        File jsonFileLessons = selectedSemester != null ?
                new File(jsonFolder.getAbsolutePath() +
                        "/" + selectedSemester.getId() + ".json") : null;
        try {
            getLessons(jsonFileLessons);
        } catch (FileNotFoundException fnfeLessons1) {
            viewPager.animate().alpha(0);
            llProgress.animate().alpha(1);
            tvProgressMessage.setText(R.string.getting_lessons_list);

            Ion.with(this)
                    .load(ServerConstants.SERVER_URL +
                            ServerConstants.ROOT_FOLDER +
                            groupId +
                            "/" + selectedSemester.getId() + ".json")
                    .write(jsonFileLessons)
                    .setCallback(this);
        }
    }

    private void getLessons(File jsonFile) throws FileNotFoundException {
        if (jsonFile != null) {
            lessons = Converters.registerLocalTime(new GsonBuilder()).create().fromJson(
                    new FileReader(jsonFile),
                    new TypeToken<ArrayList<Lesson>>() {
                    }.getType());

            Collections.sort(lessons, new Comparator<Lesson>() {
                @Override
                public int compare(Lesson lhs, Lesson rhs) {
                    return Minutes.minutesBetween(rhs.getStartTime(),
                            lhs.getStartTime()).getMinutes();
                }
            });
        } else
            lessons = new ArrayList<>();

        SchedulePageFragment.setLessons(lessons, selectedSemester);

        pagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), selectedSemester);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(savedPage != -1 ? savedPage : pagerAdapter.START_PAGE, false);
        savedPage = -1;
        SchedulePageFragment.setPagerAdapter(pagerAdapter);

        llProgress.animate().alpha(0);
        viewPager.animate().alpha(1);
    }
}
