package ru.erdenian.studentassistant.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.common.base.Joiner
import org.jetbrains.anko.startActivity
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.LessonEditorActivity
import ru.erdenian.studentassistant.activity.LessonInformationActivity
import ru.erdenian.studentassistant.schedule.LessonRepeat
import ru.erdenian.studentassistant.schedule.ScheduleManager

class SchedulePageFragment : Fragment() {

    companion object {

        private const val TIME_FORMAT = "HH:mm"

        private const val PAGE_SEMESTER_ID = "page_semester_id"
        private const val PAGE_DATE = "page_date"
        private const val PAGE_WEEKDAY = "page_weekday"
        private const val SHOW_WEEKS_AND_DATES = "show_weeks_and_dates"

        fun newInstance(semesterId: Long, date: LocalDate): SchedulePageFragment {
            val schedulePageFragment = SchedulePageFragment()
            val arguments = Bundle()
            with(arguments) {
                putLong(PAGE_SEMESTER_ID, semesterId)
                putString(PAGE_DATE, date.toString())
                putBoolean(SHOW_WEEKS_AND_DATES, false)
            }
            schedulePageFragment.arguments = arguments
            return schedulePageFragment
        }

        fun newInstance(semesterId: Long, weekday: Int): SchedulePageFragment {
            val schedulePageFragment = SchedulePageFragment()
            val arguments = Bundle()
            with(arguments) {
                putLong(PAGE_SEMESTER_ID, semesterId)
                arguments.putInt(PAGE_WEEKDAY, weekday)
                putBoolean(SHOW_WEEKS_AND_DATES, true)
            }
            schedulePageFragment.arguments = arguments
            return schedulePageFragment
        }
    }

    private val semesterId: Long by lazy { arguments.getLong(PAGE_SEMESTER_ID, -1L) }
    private val showWeeksAndDates: Boolean by lazy { arguments.getBoolean(SHOW_WEEKS_AND_DATES) }
    private val day: LocalDate by lazy { LocalDate(arguments.getString(PAGE_DATE)) }
    private val weekday: Int by lazy { arguments.getInt(PAGE_WEEKDAY, -1) }

    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val lessons = if (showWeeksAndDates)
            ScheduleManager.getLessons(semesterId, weekday)
        else
            ScheduleManager.getLessons(semesterId, day)

        if (lessons.isEmpty()) {
            return inflater.inflate(R.layout.fragment_free_day, container, false)
        }

        val view = inflater.inflate(R.layout.scroll_view, container, false)
        val llCardsParent = view.findViewById(R.id.scroll_view_items_parent) as LinearLayout

        for ((subjectName, type, teachers, classrooms, startTime, endTime, lessonRepeat, id) in lessons) {
            with(inflater.inflate(R.layout.card_schedule, llCardsParent, false)) {
                (findViewById(R.id.card_schedule_start_time) as TextView).text = startTime.toString(TIME_FORMAT)
                (findViewById(R.id.card_schedule_end_time) as TextView).text = endTime.toString(TIME_FORMAT)

                if (classrooms.isNotEmpty()) {
                    (findViewById(R.id.card_schedule_classrooms) as TextView).text = Joiner.on(", ").join(classrooms)
                } else {
                    findViewById(R.id.card_schedule_classrooms_icon).visibility = View.GONE
                }

                with(findViewById(R.id.card_schedule_type) as TextView) {
                    if (type.isNotBlank()) text = type
                    else visibility = View.GONE
                }

                (findViewById(R.id.card_schedule_name) as TextView).text = subjectName

                with(findViewById(R.id.card_schedule_teachers_parent) as LinearLayout) {
                    teachers.forEach {
                        val teacher = inflater.inflate(R.layout.textview_teacher, this, false)
                        (teacher.findViewById(R.id.textview_teacher) as TextView).text = it
                        addView(teacher)
                    }
                }

                if (!showWeeksAndDates) {
                    (findViewById(R.id.card_schedule_repeats) as LinearLayout).visibility = View.GONE
                } else {
                    (findViewById(R.id.card_schedule_repeats_data) as TextView).text =
                            when (lessonRepeat) {
                                is LessonRepeat.ByWeekday -> {
                                    val weeks = mutableListOf<Int>()
                                    for ((i, w) in lessonRepeat.weeks.withIndex())
                                        if (w) weeks.add(i + 1)

                                    getString(R.string.schedule_page_fragment_weeks) + " " + Joiner.on(", ").join(weeks) + " " +
                                            getString(R.string.schedule_page_fragment_out_of) + " " + lessonRepeat.weeks.size
                                }
                                is LessonRepeat.ByDates -> Joiner.on(", ").join(lessonRepeat.dates)
                                else -> throw IllegalStateException("Неизвестный тип повторения: $lessonRepeat")
                            }
                }

                if (showWeeksAndDates) setOnClickListener {
                    context.startActivity<LessonEditorActivity>(
                            LessonEditorActivity.SEMESTER_ID to semesterId,
                            LessonEditorActivity.LESSON_ID to id
                    )
                }
                else setOnClickListener {
                    context.startActivity<LessonInformationActivity>(
                            LessonInformationActivity.SEMESTER_ID to semesterId,
                            LessonInformationActivity.LESSON_ID to id)
                }

                llCardsParent.addView(this)
            }
        }

        return view
    }
}
