package ru.erdenian.studentassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.schedule.Homework;
import ru.erdenian.studentassistant.schedule.Lesson;
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener;
import ru.erdenian.studentassistant.schedule.ScheduleManager;
import ru.erdenian.studentassistant.schedule.Semester;

public class SemestersEditorActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        OnScheduleUpdateListener, View.OnClickListener {

    ListView lvSemesters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semesters_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvSemesters = (ListView) findViewById(R.id.content_semesters_editor_semesters_list);
        lvSemesters.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_semesters_editor_add_semester);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScheduleManager.setOnScheduleUpdateListener(this);
        onScheduleUpdate();
    }

    @Override
    public void onScheduleUpdate() {
        int index = lvSemesters.getFirstVisiblePosition();
        View v = lvSemesters.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - lvSemesters.getPaddingTop());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ScheduleManager.getSemestersNames());
        lvSemesters.setAdapter(adapter);

        lvSemesters.setSelectionFromTop(index, top);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + item.getItemId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, LessonsEditorActivity.class);
        intent.putExtra(LessonsEditorActivity.SEMESTER_INDEX, i);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_semesters_editor_add_semester:
                List<Semester> semesters = new ArrayList<>(ScheduleManager.getSemesters().asList());
                semesters.add(new Semester("Семестр " + System.currentTimeMillis(), new LocalDate(2017, 9, 1), new LocalDate(2017, 12, 31),
                        ImmutableSortedSet.<Lesson>of(), ImmutableSortedSet.<Homework>of()));
                ScheduleManager.setSemesters(ImmutableSortedSet.copyOf(semesters));
                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + view.getId());
                break;
        }
    }
}
