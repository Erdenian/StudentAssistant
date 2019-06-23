package ru.erdenian.studentassistant.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.utils.setViewCount

/**
 * Карточка пары.
 *
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see CardView
 * @since 0.2.6
 */
class LessonCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }

    private val tvStartTime: TextView
    private val tvEndTime: TextView
    private val llClassroomsParent: LinearLayout
    private val tvClassrooms: TextView
    private val tvType: TextView
    private val tvSubjectName: TextView
    private val llTeachersParent: LinearLayout
    private val llRepeatsParent: LinearLayout
    private val tvRepeatsText: TextView

    init {
        inflate(context, R.layout.lesson_card, this)

        TypedValue().also { outValue ->
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            foreground = context.getDrawable(outValue.resourceId)
        }

        tvStartTime = findViewById(R.id.lc_start_time)
        tvEndTime = findViewById(R.id.lc_end_time)
        llClassroomsParent = findViewById(R.id.lc_classrooms_parent)
        tvClassrooms = findViewById(R.id.lc_classrooms)
        tvType = findViewById(R.id.lc_type)
        tvSubjectName = findViewById(R.id.lc_subject_name)
        llTeachersParent = findViewById(R.id.lc_teachers_parent)
        llRepeatsParent = findViewById(R.id.lc_repeats_parent)
        tvRepeatsText = findViewById(R.id.lc_repeats_text)
    }

    /**
     * Заполняет элементы интерфейса в соответствии с переданной парой.
     *
     * @since 0.2.6
     */
    fun setLesson(
        subjectName: String,
        type: String?,
        teachers: List<String>,
        classrooms: List<String>,
        startTime: LocalTime,
        endTime: LocalTime
    ) {
        tvStartTime.text = startTime.toString(TIME_FORMAT)
        tvEndTime.text = endTime.toString(TIME_FORMAT)

        llClassroomsParent.visibility = if (classrooms.isNotEmpty()) View.VISIBLE else View.GONE
        tvClassrooms.text = classrooms.joinToString()

        tvType.visibility = if (type?.isNotBlank() == true) View.VISIBLE else View.GONE
        tvType.text = type

        tvSubjectName.text = subjectName

        llTeachersParent.visibility = if (teachers.isNotEmpty()) View.VISIBLE else View.GONE
        llTeachersParent.setViewCount(
            teachers.size,
            { TeacherView(context) },
            { name = teachers[it] }
        )
    }
}
