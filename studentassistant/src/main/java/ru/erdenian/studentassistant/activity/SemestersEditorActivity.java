package ru.erdenian.studentassistant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import ru.erdenian.studentassistant.ulils.UiUtils;

public class SemestersEditorActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        OnScheduleUpdateListener {

    ListView lvSemesters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semesters_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_semesters_editor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvSemesters = (ListView) findViewById(R.id.content_semesters_editor_semesters_list);
        lvSemesters.setOnItemClickListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_semesters_editor, menu);
        UiUtils.colorMenu(this, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_semesters_editor_add_semester:
                Toast.makeText(this, R.string.menu_semesters_editor_add_semeseter, Toast.LENGTH_SHORT).show();

                List<Semester> semesters = new ArrayList<>(ScheduleManager.getSemesters().asList());
                semesters.add(new Semester("Семестр " + System.currentTimeMillis(), new LocalDate(2017, 9, 1), new LocalDate(2017, 12, 31),
                        ImmutableSortedSet.<Lesson>of(), ImmutableSortedSet.<Homework>of()));
                ScheduleManager.setSemesters(ImmutableSortedSet.copyOf(semesters));

                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + item.getItemId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, ScheduleManager.getSemesters().asList().get(i).getName(), Toast.LENGTH_SHORT).show();
    }
}
