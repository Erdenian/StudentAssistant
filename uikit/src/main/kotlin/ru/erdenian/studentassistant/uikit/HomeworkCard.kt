package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.utils.id

/**
 * Карточка домашнего задания
 *
 * @see MaterialCardView
 *
 * @version 1.0.0
 * @author Ilya Solovyov
 * @since 0.3.0
 */
class HomeworkCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {

    companion object {
        private const val DATE_FORMATTER = "dd.MM.yyyy"
    }

    private val subjectName: TextView by id(R.id.ch_subject_name)
    private val description: TextView by id(R.id.ch_description)
    private val deadline: TextView by id(R.id.ch_deadline)

    init {
        inflate(context, R.layout.card_homework, this)
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
    fun setHomework(subjectName: String, description: String, deadline: LocalDate) {
        this.subjectName.text = subjectName
        this.description.text = description
        this.deadline.text = deadline.toString(DATE_FORMATTER)
    }
}
