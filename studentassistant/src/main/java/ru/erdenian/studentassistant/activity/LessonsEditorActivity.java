package ru.erdenian.studentassistant.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.schedule.Lesson;
import ru.erdenian.studentassistant.schedule.ScheduleManager;
import ru.erdenian.studentassistant.schedule.Semester;

public class LessonsEditorActivity extends AppCompatActivity implements View.OnClickListener {

    static final String SEMESTER_INDEX = "semester_index";

    private int semesterIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_lessons_editor_add_lesson);
        fab.setOnClickListener(this);

        semesterIndex = getIntent().getExtras().getInt(SEMESTER_INDEX, -1);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_lessons_editor_add_lesson:
                Semester semester = ScheduleManager.getSemesters().asList().get(semesterIndex);
                List<Lesson> lessons = new ArrayList<>(semester.getLessons());

                lessons.add(new Lesson("Конструирование ПО", "Лабораторная работа",
                        ImmutableSortedSet.of("Федоров Алексей Роальдович", "Федоров Петр Алексеевич"), ImmutableSortedSet.of("4212а"),
                        new LocalTime(18, 20), new LocalTime(18, 50),
                        Lesson.RepeatType.BY_WEEKDAY, 5, ImmutableList.of(false, true), null));

                Semester newSemester = new Semester(semester.getId(), semester.getName(), semester.getFirstDay(), semester.getLastDay(),
                        ImmutableSortedSet.copyOf(lessons), semester.getHomeworks());

                List<Semester> semesters = new ArrayList<>(ScheduleManager.getSemesters().asList());
                semesters.set(semesterIndex, newSemester);

                ScheduleManager.setSemesters(ImmutableSortedSet.copyOf(semesters));
                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + view.getId());
                break;
        }
    }
}
