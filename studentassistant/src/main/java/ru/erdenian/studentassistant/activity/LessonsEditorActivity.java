package ru.erdenian.studentassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.adapter.ScheduleEditorPagerAdapter;
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener;
import ru.erdenian.studentassistant.schedule.ScheduleManager;

public class LessonsEditorActivity extends AppCompatActivity implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        OnScheduleUpdateListener {

    static final String SEMESTER_ID = "semester_id";

    private long semesterId = -1;
    private int semesterIndex = -1;

    private int savedPage = 0;

    private ViewPager viewPager;
    private ScrollView scrollView;
    private ScheduleEditorPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_with_spinner);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Spinner spEditTypes = (Spinner) findViewById(R.id.toolbar_with_spinner_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_semesters,
                getResources().getStringArray(R.array.activity_lessons_editor_edit_types));
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_semesters);
        spEditTypes.setAdapter(adapter);
        spEditTypes.setOnItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.view_pager_pager_tab_strip);
        pagerTabStrip.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_lessons_editor_add_lesson);
        fab.setOnClickListener(this);

        semesterId = getIntent().getLongExtra(SEMESTER_ID, -1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScheduleManager.INSTANCE.setOnScheduleUpdateListener(this);
        onScheduleUpdate();
    }

    @Override
    public void onScheduleUpdate() {
        if ((semesterIndex == -1) || (semesterIndex >= ScheduleManager.INSTANCE.getSemesters().size()) ||
                (ScheduleManager.INSTANCE.getSemesters().asList().get(semesterIndex).getId() != semesterId)) {
            semesterIndex = ScheduleManager.INSTANCE.getSemesterIndex(semesterId);

            if (semesterIndex == -1) {
                finish();
                return;
            }
        }

        if (pagerAdapter != null) {
            savedPage = viewPager.getCurrentItem();
        }

        pagerAdapter = new ScheduleEditorPagerAdapter(getSupportFragmentManager(), ScheduleManager.INSTANCE.getSemesters().asList().get(semesterIndex));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(savedPage, false);
        savedPage = 0;

        // TODO: 13.11.2016 добавить заполнение списка пар по датам
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lessons_editor, menu);
        //UiUtils.INSTANCE.colorMenu(this, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        viewPager.setVisibility((i == 0) ? View.VISIBLE : View.GONE);
        scrollView.setVisibility((i == 1) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_lessons_editor_edit_semester:
                Intent intent = new Intent(this, SemesterEditorActivity.class);
                intent.putExtra(SemesterEditorActivity.SEMESTER_ID, semesterId);
                startActivity(intent);
                break;
            case R.id.menu_lessons_editor_delete_semester:
                ScheduleManager.INSTANCE.removeSemester(semesterIndex);
                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + item.getItemId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_lessons_editor_add_lesson:
                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + v.getId());
                break;
        }
    }
}
