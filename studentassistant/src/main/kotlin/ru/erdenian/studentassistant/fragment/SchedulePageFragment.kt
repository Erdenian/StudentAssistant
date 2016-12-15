package ru.erdenian.studentassistant.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableSortedSet
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.LessonEditorActivity
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

class SchedulePageFragment : Fragment() {

    companion object {

        private const val TIME_FORMAT = "HH:mm"

        private const val PAGE_SEMESTER_ID = "page_semester_id"
        private const val PAGE_DATE = "page_date"
        private const val PAGE_WEEKDAY = "page_weekday"
        private const val SHOW_WEEKS_AND_DATES = "show_weeks_and_dates"

        fun newInstance(semester: Semester, date: LocalDate): SchedulePageFragment {
            val schedulePageFragment = SchedulePageFragment()
            val arguments = Bundle()
            with(arguments) {
                putLong(PAGE_SEMESTER_ID, semester.id)
                putString(PAGE_DATE, date.toString())
                putBoolean(SHOW_WEEKS_AND_DATES, false)
            }
            schedulePageFragment.arguments = arguments
            return schedulePageFragment
        }

        fun newInstance(semester: Semester, weekday: Int): SchedulePageFragment {
            val schedulePageFragment = SchedulePageFragment()
            val arguments = Bundle()
            with(arguments) {
                putLong(PAGE_SEMESTER_ID, semester.id)
                arguments.putInt(PAGE_WEEKDAY, weekday)
                putBoolean(SHOW_WEEKS_AND_DATES, true)
            }
            schedulePageFragment.arguments = arguments
            return schedulePageFragment
        }
    }

    private val semester: Semester? by lazy { ScheduleManager[arguments.getLong(PAGE_SEMESTER_ID)] }
    private val day: LocalDate by lazy { LocalDate(arguments.getString(PAGE_DATE)) }
    private val weekday: Int by lazy { arguments.getInt(PAGE_WEEKDAY, -1) }
    private val showWeeksAndDates: Boolean by lazy { arguments.getBoolean(SHOW_WEEKS_AND_DATES) }

    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*val lessons = (if (showWeeksAndDates) semester?.getLessons(weekday) else semester?.getLessons(day)) ?:
                return inflater.inflate(R.layout.fragment_free_day, container, false)*/
        val lessons = ImmutableSortedSet.of<Lesson>()

        if (lessons.isEmpty()) {
            return inflater.inflate(R.layout.fragment_free_day, container, false)
        }

        val view = inflater.inflate(R.layout.scroll_view, container, false)
        val llCardsParent = view.findViewById(R.id.scroll_view_items_parent) as LinearLayout

        for (lesson in lessons) {
            with(inflater.inflate(R.layout.card_schedule, llCardsParent, false)) {
                (findViewById(R.id.card_schedule_start_time) as TextView).text = lesson.startTime.toString(TIME_FORMAT)
                (findViewById(R.id.card_schedule_end_time) as TextView).text = lesson.endTime.toString(TIME_FORMAT)

                if (lesson.classrooms != null) {
                    (findViewById(R.id.card_schedule_classrooms) as TextView).text = Joiner.on(", ").join(lesson.classrooms)
                } else {
                    findViewById(R.id.card_schedule_classrooms_icon).visibility = View.GONE
                }

                with(findViewById(R.id.card_schedule_type) as TextView) {
                    if (lesson.type != null) text = lesson.type
                    else visibility = View.GONE
                }

                (findViewById(R.id.card_schedule_name) as TextView).text = lesson.subjectName

                with(findViewById(R.id.card_schedule_teachers_parent) as LinearLayout) {
                    lesson.teachers?.let {
                        for (teacherName in it) {
                            val teacher = inflater.inflate(R.layout.textview_teacher, this, false)
                            (teacher.findViewById(R.id.textview_teacher) as TextView).text = teacherName
                            addView(teacher)
                        }
                    }
                }

                if (!showWeeksAndDates) {
                    (findViewById(R.id.card_schedule_repeats) as LinearLayout).visibility = View.GONE
                } else {
                    (findViewById(R.id.card_schedule_repeats_data) as TextView).text =
                            /*when (lesson.repeatType) {
                                Lesson.RepeatType.BY_WEEKDAY -> {
                                    val weeks = mutableListOf<Int>()
                                    for ((i, w) in lesson.weeks!!.withIndex())
                                        if (w) weeks.add(i + 1)
                                    getString(R.string.schedule_page_fragment_weeks) + " " + Joiner.on(", ").join(weeks) + " " +
                                            getString(R.string.schedule_page_fragment_out_of) + " " + lesson.weeks.size
                                }
                                Lesson.RepeatType.BY_DATE -> Joiner.on(", ").join(lesson.dates)
                                else -> throw IllegalStateException("Неизвестный тип повторения: ${lesson.repeatType}")
                            }*/ "hui"
                }

                if (showWeeksAndDates) setOnClickListener {
                    with(Intent(context, LessonEditorActivity::class.java)) {
                        putExtra(LessonEditorActivity.SEMESTER_ID, semester!!.id)
                        putExtra(LessonEditorActivity.LESSON_ID, lesson.id)
                        startActivity(this)
                    }
                }
                else setOnClickListener { context.toast(lesson.subjectName) }

                llCardsParent.addView(this)
            }
        }

        return view
    }
}
