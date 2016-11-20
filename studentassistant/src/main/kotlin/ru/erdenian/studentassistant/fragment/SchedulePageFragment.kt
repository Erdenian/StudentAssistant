package ru.erdenian.studentassistant.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.common.base.Joiner
import org.jetbrains.anko.toast
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.schedule.ScheduleManager
import ru.erdenian.studentassistant.schedule.Semester

/**
 * Todo: описание класса.
 *
 * @author Ilya Solovyev
 * @version 0.0.0
 * @since 0.0.0
 */
class SchedulePageFragment : Fragment() {

    companion object {

        private const val PAGE_SEMESTER_ID = "page_semester_id"
        private const val PAGE_DATE = "page_date"
        private const val TIME_FORMAT = "HH:mm"

        fun newInstance(semesterId: Long, date: LocalDate): SchedulePageFragment {
            val schedulePageFragment = SchedulePageFragment()
            val arguments = Bundle()
            with(arguments) {
                putLong(PAGE_SEMESTER_ID, semesterId)
                putString(PAGE_DATE, date.toString())
            }
            schedulePageFragment.arguments = arguments
            return schedulePageFragment
        }
    }

    private lateinit var semester: Semester
    private lateinit var day: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        semester = ScheduleManager.getSemester(arguments.getLong(PAGE_SEMESTER_ID, -1))
        day = LocalDate(arguments.getString(PAGE_DATE))
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {

        val lessons = semester.getLessons(day)

        if (lessons.isEmpty()) {
            return inflater.inflate(R.layout.fragment_free_day, container, false)
        }

        val view = inflater.inflate(R.layout.scroll_view, container, false)
        val llCardsParent = view.findViewById(R.id.scroll_view_items_parent) as LinearLayout

        for ((name, type, teachers, classrooms, startTime, endTime) in lessons) {
            val card = inflater.inflate(R.layout.card_schedule, llCardsParent, false)

            val tvStartTime = card.findViewById(R.id.card_schedule_start_time) as TextView
            val tvEndTime = card.findViewById(R.id.card_schedule_end_time) as TextView
            val tvClassrooms = card.findViewById(R.id.card_schedule_classrooms) as TextView
            val tvType = card.findViewById(R.id.card_schedule_type) as TextView
            val tvName = card.findViewById(R.id.card_schedule_name) as TextView

            tvStartTime.text = startTime.toString(TIME_FORMAT)
            tvEndTime.text = endTime.toString(TIME_FORMAT)

            if (classrooms.size > 0) {
                tvClassrooms.text = Joiner.on(", ").join(classrooms)
            } else {
                card.findViewById(R.id.card_schedule_classrooms_icon).visibility = View.GONE
            }

            if (type != null) tvType.text = type
            else tvType.height = 0

            tvName.text = name

            val llTeachersParent = card.findViewById(R.id.card_schedule_teachers_parent) as LinearLayout

            for (teacherName in teachers) {
                val teacher = inflater.inflate(R.layout.textview_teacher, llTeachersParent, false)
                (teacher.findViewById(R.id.textview_teacher) as TextView).text = teacherName
                llTeachersParent.addView(teacher)
            }

            card.setOnClickListener { context.toast(name) }

            llCardsParent.addView(card)
        }

        return view
    }
}
