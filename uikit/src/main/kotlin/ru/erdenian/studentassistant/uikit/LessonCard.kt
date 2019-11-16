package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.utils.id
import ru.erdenian.studentassistant.utils.setViewCount

/**
 * Карточка пары
 *
 * @see MaterialCardView
 *
 * @version 1.0.0
 * @author Ilya Solovyov
 * @since 0.2.6
 */
class LessonCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    companion object {
        private const val TIME_FORMATTER = "HH:mm"
    }

    private val tvStartTime: TextView by id(R.id.lc_start_time)
    private val tvEndTime: TextView by id(R.id.lc_end_time)
    private val llClassroomsParent: LinearLayout by id(R.id.lc_classrooms_parent)
    private val tvClassrooms: TextView by id(R.id.lc_classrooms)
    private val tvType: TextView by id(R.id.lc_type)
    private val tvSubjectName: TextView by id(R.id.lc_subject_name)
    private val llTeachersParent: LinearLayout by id(R.id.lc_teachers_parent)
    private val llRepeatsParent: LinearLayout by id(R.id.lc_repeats_parent)
    private val tvRepeatsText: TextView by id(R.id.lc_repeats_text)

    init {
        inflate(context, R.layout.card_lesson, this)
        TypedValue().also { outValue ->
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            foreground = context.getDrawable(outValue.resourceId)
        }
    }

    /**
     * Заполняет элементы интерфейса в соответствии с переданными данными
     *
     * @since 0.3.0
     */
    @Suppress("LongParameterList")
    fun setLesson(
        subjectName: String,
        type: String?,
        teachers: List<String>,
        classrooms: List<String>,
        startTime: LocalTime,
        endTime: LocalTime
    ) {
        tvStartTime.text = startTime.toString(TIME_FORMATTER)
        tvEndTime.text = endTime.toString(TIME_FORMATTER)

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
