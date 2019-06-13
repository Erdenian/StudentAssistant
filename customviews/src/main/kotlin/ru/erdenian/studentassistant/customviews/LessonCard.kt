package ru.erdenian.studentassistant.customviews

import android.content.Context
import android.util.AttributeSet
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
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    companion object {
        private const val TIME_FORMAT = "HH:mm"
    }

    private val tvStartTime: TextView = findViewById(R.id.lc_start_time)
    private val tvEndTime: TextView = findViewById(R.id.lc_end_time)
    private val llClassroomsParent: LinearLayout = findViewById(R.id.lc_classrooms_parent)
    private val tvClassrooms: TextView = findViewById(R.id.lc_classrooms)
    private val tvType: TextView = findViewById(R.id.lc_type)
    private val tvSubjectName: TextView = findViewById(R.id.lc_subject_name)
    private val llTeachersParent: LinearLayout = findViewById(R.id.lc_teachers_parent)
    private val llRepeatsParent: LinearLayout = findViewById(R.id.lc_repeats_parent)
    private val tvRepeatsText: TextView = findViewById(R.id.lc_repeats_text)

    init {
        inflate(context, R.layout.lesson_card, this)
    }

    /**
     * Заполняет элементы интерфейса в соответствии с переданной парой.
     *
     * @since 0.2.6
     */
    fun setLesson(
        subjectName: String,
        type: String,
        teachers: List<String>,
        classrooms: List<String>,
        startTime: LocalTime,
        endTime: LocalTime
    ) {
        tvStartTime.text = startTime.toString(TIME_FORMAT)
        tvEndTime.text = endTime.toString(TIME_FORMAT)

        llClassroomsParent.visibility = if (classrooms.isNotEmpty()) View.VISIBLE else View.GONE
        tvClassrooms.text = classrooms.joinToString()

        tvType.visibility = if (type.isNotBlank()) View.VISIBLE else View.GONE
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
