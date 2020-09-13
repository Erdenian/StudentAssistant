package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.getSystemService
import com.google.android.material.card.MaterialCardView
import org.joda.time.LocalDate
import ru.erdenian.studentassistant.uikit.databinding.CardHomeworkBinding

/**
 * Карточка домашнего задания.
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

    private val binding = CardHomeworkBinding.inflate(requireNotNull(context.getSystemService()), this)

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
    fun setHomework(subjectName: String, description: String, deadline: LocalDate) {
        binding.subjectName.text = subjectName
        binding.description.text = description
        binding.deadline.text = deadline.toString(DATE_FORMATTER)
    }
}
