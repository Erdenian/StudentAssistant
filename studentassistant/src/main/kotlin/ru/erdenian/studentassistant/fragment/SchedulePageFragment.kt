package ru.erdenian.studentassistant.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.COPY
import ru.erdenian.studentassistant.activity.LESSON_ID
import ru.erdenian.studentassistant.activity.LessonEditorActivity
import ru.erdenian.studentassistant.activity.SEMESTER_ID
import ru.erdenian.studentassistant.adapter.ScheduleListAdapter
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

    val view = inflater.inflate(R.layout.list_view, container, false) as ListView
    view.adapter = ScheduleListAdapter(context, semesterId, showWeeksAndDates, lessons)
    if (showWeeksAndDates) registerForContextMenu(view)

    return view
  }

  override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
    super.onCreateContextMenu(menu, v, menuInfo)
    activity.menuInflater.inflate(R.menu.context_menu_schedule_page_fragment, menu)

    val info = menuInfo as AdapterView.AdapterContextMenuInfo
    menu.setHeaderTitle(ScheduleManager.getLesson(semesterId, info.id)!!.subjectName)
  }

  override fun onContextItemSelected(item: MenuItem): Boolean {
    val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
    val lesson = ScheduleManager.getLesson(semesterId, info.id)!!
    when (item.itemId) {
      R.id.context_menu_schedule_page_fragment_copy -> context.startActivity<LessonEditorActivity>(
          context.SEMESTER_ID to semesterId,
          context.LESSON_ID to lesson.id,
          context.COPY to true
      )
      R.id.context_menu_schedule_page_fragment_delete -> {
        fun remove() {
          ScheduleManager.removeLesson(semesterId, lesson.id)
          //context.startService<ScheduleService>()
        }

        if (ScheduleManager.getHomeworks(semesterId, lesson.subjectName).isNotEmpty()
            && (ScheduleManager.getLessons(semesterId, lesson.subjectName).size == 1)) {

          context.alert(R.string.activity_lesson_editor_alert_delete_homeworks_message,
              R.string.activity_lesson_editor_alert_delete_homeworks_title) {
            positiveButton(R.string.activity_lesson_editor_alert_delete_homeworks_yes) { remove() }
            neutralButton(R.string.activity_lesson_editor_alert_delete_homeworks_cancel)
          }.show()
        } else {

          context.alert(R.string.activity_lesson_editor_alert_delete_message) {
            positiveButton(R.string.activity_lesson_editor_alert_delete_yes) { remove() }
            negativeButton(R.string.activity_lesson_editor_alert_delete_no)
          }.show()
        }
      }
      else -> return super.onContextItemSelected(item)
    }
    return true
  }
}
