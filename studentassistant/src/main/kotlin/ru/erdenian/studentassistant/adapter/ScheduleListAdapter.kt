package ru.erdenian.studentassistant.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.common.collect.ImmutableSortedSet
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.activity.LESSON_ID
import ru.erdenian.studentassistant.activity.LessonEditorActivity
import ru.erdenian.studentassistant.activity.LessonInformationActivity
import ru.erdenian.studentassistant.activity.SEMESTER_ID
import ru.erdenian.studentassistant.customviews.LessonCard
import ru.erdenian.studentassistant.schedule.Lesson

class ScheduleListAdapter(private val context: Context, val semesterId: Long, val showWeeksAndDates: Boolean,
                          val lessons: ImmutableSortedSet<Lesson>) : BaseAdapter() {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
      LessonCard(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val lesson = lessons.asList()[position]
        setEditing(showWeeksAndDates)
        setLesson(lesson)

        if (!showWeeksAndDates) {
          setOnClickListener {
            context.startActivity<LessonInformationActivity>(
                context.SEMESTER_ID to semesterId,
                context.LESSON_ID to lesson.id
            )
          }
        } else {
          isLongClickable = true
          setOnClickListener {
            context.startActivity<LessonEditorActivity>(
                context.SEMESTER_ID to semesterId,
                context.LESSON_ID to lesson.id
            )
          }
        }
      }

  override fun getItem(position: Int): Any = lessons.asList()[position]

  override fun getItemId(position: Int): Long = lessons.asList()[position].id

  override fun getCount(): Int = lessons.size
}