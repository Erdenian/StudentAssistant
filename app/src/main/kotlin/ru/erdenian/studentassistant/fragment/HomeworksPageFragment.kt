package ru.erdenian.studentassistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.jetbrains.anko.startActivity
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.activity.HOMEWORK_ID
import ru.erdenian.studentassistant.activity.HomeworkEditorActivity
import ru.erdenian.studentassistant.activity.SEMESTER_ID
import ru.erdenian.studentassistant.localdata.ScheduleManager

class HomeworksPageFragment : Fragment() {

  companion object {

    private const val PAGE_SEMESTER_ID = "page_semester_id"
    private const val PAGE = "page"

    fun newInstance(semesterId: Long, page: Int): HomeworksPageFragment {
      val homeworksPageFragment = HomeworksPageFragment()
      val arguments = Bundle()
      with(arguments) {
        putLong(PAGE_SEMESTER_ID, semesterId)
        putInt(PAGE, page)
      }
      homeworksPageFragment.arguments = arguments
      return homeworksPageFragment
    }
  }

  private val semesterId: Long by lazy { arguments!!.getLong(PAGE_SEMESTER_ID, -1L) }
  private val page: Int by lazy { arguments!!.getInt(PAGE, -1) }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val homeworks = when (page) {
      0 -> ScheduleManager.getActualHomeworks(semesterId)
      1 -> ScheduleManager.getPastHomeworks(semesterId)
      else -> throw IllegalArgumentException("Неизвестный номер страницы: $page")
    }

    if (homeworks.isEmpty()) {
      return inflater.inflate(R.layout.fragment_no_homeworks, container, false)
    }

    val view = inflater.inflate(R.layout.scroll_view, container, false)
    val llCardsParent = view.findViewById<LinearLayout>(R.id.scroll_view_items_parent)

    for ((subjectName, description, deadline, id) in homeworks) {
      with(inflater.inflate(R.layout.card_homework, llCardsParent, false)) {
        (findViewById<TextView>(R.id.card_homework_subject_name)).text = subjectName
        (findViewById<TextView>(R.id.card_homework_description)).text = description
        (findViewById<TextView>(R.id.card_homework_deadline)).text = deadline.toString("dd.MM.yyyy")

        setOnClickListener {
          context.startActivity<HomeworkEditorActivity>(
              context.SEMESTER_ID to semesterId,
              context.HOMEWORK_ID to id
          )
        }

        llCardsParent.addView(this)
      }
    }

    return view
  }
}