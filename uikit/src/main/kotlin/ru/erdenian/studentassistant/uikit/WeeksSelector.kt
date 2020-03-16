package ru.erdenian.studentassistant.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.content.getSystemService
import androidx.core.view.children
import androidx.core.view.forEach
import ru.erdenian.studentassistant.uikit.databinding.WeeksSelectorBinding
import ru.erdenian.studentassistant.utils.setViewCount

/**
 * View для выбора недель для повторения пары.
 *
 * Состоит из спиннера с предустановленными вариантами и чекбоксов для самостоятельного
 * выбора недель, если ни один из предустановленных вариантов не подходит.
 *
 * @see Spinner
 * @see CheckBoxWithText
 *
 * @version 1.0.0
 * @author Ilya Solovyov
 * @since 0.2.6
 */
class WeeksSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = WeeksSelectorBinding.inflate(context.getSystemService(), this)

    /**
     * Список предустановленных вариантов.
     *
     * @since 0.2.6
     */
    private val weeksVariants = listOf(
        listOf(true),
        listOf(true, false),
        listOf(false, true),
        listOf(true, false, false, false),
        listOf(false, true, false, false),
        listOf(false, false, true, false),
        listOf(false, false, false, true)
    )

    /**
     * Возвращает номер выбранного варианта из предустановок.
     *
     * @param weeks список недель
     * @return номер выбранного варианта
     * @since 0.2.6
     */
    private fun getWeeksVariantIndex(weeks: List<Boolean>) =
        weeksVariants.indexOf(weeks).takeIf { it >= 0 } ?: weeksVariants.size

    private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
        onWeeksChangeListener?.onWeeksChange(weeks)
    }

    private val creator: ViewGroup.(position: Int) -> CheckBoxWithText = { position ->
        CheckBoxWithText(context).apply {
            text = (position + 1).toString()
            setOnCheckedChangeListener(onCheckedChangeListener)
        }
    }

    /**
     * Список недель на текущий момент.
     *
     * Список недель - список boolean значений, где i-е значение показывает была ли выбрана
     * i-я неделя. Если список недель состоит из нескольких повторяющихся последовательностей,
     * то вернет только одну из них. Например, в случае списка
     * `{ true, false, true, true, false, true }` вернет `{ true, false, true }`.
     *
     * @since 0.2.6
     */
    var weeks: List<Boolean>
        get() {
            val weeks = binding.weeksParent.children.map { v ->
                @Suppress("UnsafeCast")
                (v as CheckBoxWithText).isChecked
            }.toList()
            cycleLengthLoop@ for (cycleLength in 1..(weeks.size / 2)) {
                if (weeks.size % cycleLength != 0) continue
                for (offset in cycleLength until weeks.size step cycleLength) {
                    for (position in 0 until cycleLength) {
                        if (weeks[position] != weeks[offset + position]) continue@cycleLengthLoop
                    }
                }
                return weeks.take(cycleLength)
            }
            return weeks
        }
        set(weeks) {
            val selection = getWeeksVariantIndex(weeks)

            binding.variants.apply {
                val listener = onItemSelectedListener
                onItemSelectedListener = null
                // При использовании setSelection(int) вызывается обработчик. Хз почему.
                setSelection(selection, true)
                onItemSelectedListener = listener
            }

            binding.weeksParent.setViewCount(weeks.size, creator) { isChecked = weeks[it] }

            setCustomEnabled(selection == weeksVariants.size)
        }

    interface OnWeeksChangeListener {
        fun onWeeksChange(weeks: List<Boolean>)
    }

    var onWeeksChangeListener: OnWeeksChangeListener? = null

    init {
        orientation = VERTICAL

        check(binding.variants.adapter.count == weeksVariants.size + 1) {
            "Несоответствие вариантов выбора и количества предустановок"
        }

        weeks = weeksVariants.first()

        binding.variants.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                weeks = weeksVariants.getOrNull(position) ?: return setCustomEnabled(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }

        binding.remove.setOnClickListener {
            binding.weeksParent.setViewCount(binding.weeksParent.childCount - 1, creator)
            binding.remove.isEnabled = binding.weeksParent.childCount > 1
            onWeeksChangeListener?.onWeeksChange(weeks)
        }
        binding.add.setOnClickListener {
            binding.weeksParent.setViewCount(binding.weeksParent.childCount + 1, creator)
            binding.remove.isEnabled = true
            binding.scroll.post { binding.scroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }
            onWeeksChangeListener?.onWeeksChange(weeks)
        }
        binding.weeksParent.setViewCount(binding.weeksParent.childCount, creator) { _ ->
            setOnCheckedChangeListener(onCheckedChangeListener)
        }
    }

    /**
     * Активирует или деактивирует элементы интерфейса, предназначенные для выбора недель вручную.
     *
     * @param enabled true для активации, false для деактивации
     * @since 0.2.6
     */
    private fun setCustomEnabled(enabled: Boolean) {
        binding.remove.isEnabled = enabled && binding.weeksParent.childCount > 1
        binding.add.isEnabled = enabled
        binding.weeksParent.forEach { it.isEnabled = enabled }
    }
}
