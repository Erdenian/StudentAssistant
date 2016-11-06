package ru.erdenian.studentassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.erdenian.studentassistant.ulils.FileUtils;

/**
 * Activity, открывающееся при запуске приложения.
 * Показывает картинку на весь экран, пока подгружается следующее Activity.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileUtils.initialize(this);

        /*ImmutableSortedSet<Lesson> lessons = ImmutableSortedSet.of(
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
                        lessons, ImmutableSortedSet.<Homework>of()),
                new Semester("Семестр 6", new LocalDate(2017, 9, 1), new LocalDate(2017, 12, 31),
                        lessons, ImmutableSortedSet.<Homework>of()));

        ScheduleManager.setSemesters(semesters);*/

        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
        finish();
    }
}
