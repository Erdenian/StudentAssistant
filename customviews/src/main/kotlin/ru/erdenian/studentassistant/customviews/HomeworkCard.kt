package ru.erdenian.studentassistant.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.cardview.widget.CardView
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

/**
 * Карточка домашнего задания
 *
 * @author Ilya Solovyev
 * @version 1.0.0
 * @see CardView
 * @since 0.3.0
 */
class HomeworkCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    companion object {
        private const val DATE_FORMATTER = "dd.MM.yyyy"
    }

    private val tvSubjectName: TextView
    private val tvDescription: TextView
    private val tvDeadline: TextView

    init {
        inflate(context, R.layout.card_homework, this)

        TypedValue().also { outValue ->
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            foreground = context.getDrawable(outValue.resourceId)
        }

        tvSubjectName = requireViewByIdCompat(R.id.ch_subject_name)
        tvDescription = requireViewByIdCompat(R.id.ch_description)
        tvDeadline = requireViewByIdCompat(R.id.ch_deadline)
    }

    /**
     * Заполняет элементы интерфейса в соответствии с переданным домашним заданием.
     *
     * @since 0.3.0
     */
    fun setHomework(subjectName: String, description: String, deadline: LocalDate) {
        tvSubjectName.text = subjectName
        tvDescription.text = description
        tvDeadline.text = deadline.toString(DATE_FORMATTER)
    }
}
