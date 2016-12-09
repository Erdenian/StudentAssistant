package ru.erdenian.studentassistant.activity

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.toolbar_with_spinner.*
import kotlinx.android.synthetic.main.view_pager.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.extensions.getCompatColor
import ru.erdenian.studentassistant.extensions.initializeDrawerAndNavigationView
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class HomeWorkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        setSupportActionBar(toolbar_with_spinner)
        initializeDrawerAndNavigationView(toolbar_with_spinner)

        view_pager_pager_tab_strip.setTextColor(getCompatColor(R.color.colorPrimary))
        view_pager_pager_tab_strip.setTabIndicatorColorResource(R.color.colorPrimary)

        var homeWork = ImmutableSortedSet.of(
                Lesson("Конструирование ПО", "Лабораторная работа",
                        ImmutableSortedSet.of("Федоров Алексей Роальдович", "Федоров Петр Алексеевич"),
                        ImmutableSortedSet.of("4212а"),
                        LocalTime(14, 20), LocalTime(15, 50),
                        Lesson.RepeatType.BY_WEEKDAY, 5, ImmutableList.of(false, true), null, System.nanoTime()),
                Lesson("Конструирование ПО", "Лабораторная работа",
                        ImmutableSortedSet.of("Федоров Алексей Роальдович"), ImmutableSortedSet.of("4212а"),
                        LocalTime(18, 10), LocalTime(19, 40),
                        Lesson.RepeatType.BY_WEEKDAY, 5, ImmutableList.of(false, true), null, System.nanoTime()))
        ScheduleManager.addSemester(Semester("Семестр 5", LocalDate(2016, 9, 1), LocalDate(2016, 12, 31),
                homeWork))

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
