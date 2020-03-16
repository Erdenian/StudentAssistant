package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.getSystemService
import com.google.android.material.card.MaterialCardView
import org.joda.time.LocalTime
import ru.erdenian.studentassistant.uikit.databinding.CardLessonBinding
import ru.erdenian.studentassistant.utils.setViewCount

/**
 * Карточка пары.
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
    defStyleAttr: Int = R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {

    companion object {
        private const val TIME_FORMATTER = "HH:mm"
    }

    private val binding = CardLessonBinding.inflate(context.getSystemService(), this)

    init {
        TypedValue().also { outValue ->
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            foreground = context.getDrawable(outValue.resourceId)
        }
    }

    /**
     * Заполняет элементы интерфейса в соответствии с переданными данными.
     *
     * @since 0.3.0
     */
    fun setLesson(
        subjectName: String,
        type: String?,
        teachers: List<String>,
        classrooms: List<String>,
        startTime: LocalTime,
        endTime: LocalTime
    ) {
        binding.startTime.text = startTime.toString(TIME_FORMATTER)
        binding.endTime.text = endTime.toString(TIME_FORMATTER)

        binding.classroomsParent.visibility =
            if (classrooms.isNotEmpty()) View.VISIBLE else View.GONE
        binding.classrooms.text = classrooms.joinToString()

        binding.type.visibility = if (type?.isNotBlank() == true) View.VISIBLE else View.GONE
        binding.type.text = type

        binding.subjectName.text = subjectName

        binding.teachersParent.visibility = if (teachers.isNotEmpty()) View.VISIBLE else View.GONE
        binding.teachersParent.setViewCount(
            teachers.size,
            { TeacherView(context) },
            { name = teachers[it] }
        )
    }
}
