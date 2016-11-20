package ru.erdenian.studentassistant.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import org.joda.time.LocalDate;

import ru.erdenian.studentassistant.R;
import ru.erdenian.studentassistant.schedule.OnScheduleUpdateListener;
import ru.erdenian.studentassistant.schedule.ScheduleManager;

public class SemesterEditorActivity extends AppCompatActivity implements
        OnScheduleUpdateListener, View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener {

    static final String SEMESTER_ID = "semester_id";

    private static final String FIRST_DAY_TAG = "first_day_tag";
    private static final String LAST_DAY_TAG = "last_day_tag";


    private long semesterId = -1;
    private int semesterIndex = -1;

    private String name;
    private LocalDate firstDay;
    private LocalDate lastDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextInputLayout tilName = (TextInputLayout) findViewById(R.id.content_semester_editor_semester_name);
        EditText etName = (EditText) findViewById(R.id.content_semester_editor_semester_name_edit_text);

        Button btnFirstDay = (Button) findViewById(R.id.content_semester_editor_first_day);
        btnFirstDay.setOnClickListener(this);

        Button btnLastDay = (Button) findViewById(R.id.content_semester_editor_last_day);
        btnLastDay.setOnClickListener(this);

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


        // TODO: 13.11.2016 добавить заполнение текстовых полей
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_semester_editor_first_day:
                /*UiUtils.INSTANCE.showDatePicker(ScheduleManager.INSTANCE.getSemesters().asList().get(semesterIndex).getFirstDay(),
                        ScheduleManager.INSTANCE.getSemesters().asList().get(semesterIndex).getLastDay(), LocalDate.now(),
                        getSupportFragmentManager(), this, FIRST_DAY_TAG);*/
                break;
            case R.id.content_semester_editor_last_day:
                /*UiUtils.INSTANCE.showDatePicker(ScheduleManager.INSTANCE.getSemesters().asList().get(semesterIndex).getFirstDay(),
                        ScheduleManager.INSTANCE.getSemesters().asList().get(semesterIndex).getLastDay(), LocalDate.now(),
                        getSupportFragmentManager(), this, LAST_DAY_TAG);*/
                break;
            case R.id.content_semester_editor_save:

                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный id: " + v.getId());
                break;
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        LocalDate newDate = new LocalDate(year, monthOfYear + 1, dayOfMonth);
        switch (dialog.getTag()) {
            case FIRST_DAY_TAG:
                firstDay = newDate;
                break;
            case LAST_DAY_TAG:
                lastDay = newDate;
                break;
            default:
                Log.wtf(this.getClass().getName(), "Неизвестный тэг: " + dialog.getTag());
                break;
        }
    }
}
