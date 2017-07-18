package ru.erdenian.studentassistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableSortedSet
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.LESSON_ID
import ru.erdenian.studentassistant.activity.LessonEditorActivity
import ru.erdenian.studentassistant.activity.LessonInformationActivity
import ru.erdenian.studentassistant.activity.SEMESTER_ID
import ru.erdenian.studentassistant.schedule.Lesson
import ru.erdenian.studentassistant.schedule.LessonRepeat

class ScheduleListAdapter(context: Context, val semesterId: Long, val showWeeksAndDates: Boolean,
                          val lessons: ImmutableSortedSet<Lesson>) : BaseAdapter() {

  companion object {
    private const val TIME_FORMAT = "HH:mm"
    private val joiner = Joiner.on(", ")
  }

  private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
      (convertView ?: inflater.inflate(R.layout.card_schedule, parent, false)).apply {
        val (subjectName, type, teachers, classrooms, startTime, endTime, lessonRepeat, id) = lessons.asList()[position]

        (findViewById(R.id.card_schedule_start_time) as TextView).text = startTime.toString(TIME_FORMAT)
        (findViewById(R.id.card_schedule_end_time) as TextView).text = endTime.toString(TIME_FORMAT)

        if (classrooms.isNotEmpty()) {
          (findViewById(R.id.card_schedule_classrooms) as TextView).text = joiner.join(classrooms)
        } else {
          findViewById(R.id.card_schedule_classrooms_icon).visibility = View.GONE
        }

        with(findViewById(R.id.card_schedule_type) as TextView) {
          if (type.isNotBlank()) text = type
          else visibility = View.GONE
        }

        (findViewById(R.id.card_schedule_name) as TextView).text = subjectName

        with(findViewById(R.id.card_schedule_teachers_parent) as LinearLayout) {
          removeAllViews()
          teachers.forEach {
            val teacher = inflater.inflate(R.layout.textview_teacher, this, false)
            (teacher.findViewById(R.id.textview_teacher) as TextView).text = it
            addView(teacher)
          }
        }

        if (!showWeeksAndDates) {
          (findViewById(R.id.card_schedule_repeats) as LinearLayout).visibility = View.GONE
          setOnClickListener {
            context.startActivity<LessonInformationActivity>(
                context.SEMESTER_ID to semesterId,
                context.LESSON_ID to id
            )
          }
        } else {
          (findViewById(R.id.card_schedule_repeats_data) as TextView).text = when (lessonRepeat) {
            is LessonRepeat.ByWeekday -> context.getString(R.string.schedule_page_fragment_weeks) + " " +
                joiner.join(lessonRepeat.weeks.mapIndexedNotNull { index, b -> if (b) index + 1 else null }) + " " +
                context.getString(R.string.schedule_page_fragment_out_of) + " " + lessonRepeat.weeks.size
            is LessonRepeat.ByDates -> joiner.join(lessonRepeat.dates)
            else -> throw IllegalStateException("Неизвестный тип повторения: $lessonRepeat")
          }
          isLongClickable = true
          setOnClickListener {
            context.startActivity<LessonEditorActivity>(
                context.SEMESTER_ID to semesterId,
                context.LESSON_ID to id
            )
          }
        }
      }

  override fun getItem(position: Int): Any = lessons.asList()[position]

  override fun getItemId(position: Int): Long = lessons.asList()[position].id

  override fun getCount(): Int = lessons.size
}