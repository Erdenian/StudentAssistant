package ru.erdenian.studentassistant.ui.lessonseditor

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import ru.erdenian.studentassistant.customviews.LessonCard
import ru.erdenian.studentassistant.repository.entity.LessonNew

class LessonsEditorListAdapter : RecyclerView.Adapter<LessonsEditorListAdapter.ItemViewHolder>() {

    var lessons: List<LessonNew> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnLessonClickListener {
        fun onLessonClick(lesson: LessonNew)
    }

    var onLessonClickListener: OnLessonClickListener? = null

    override fun getItemCount() = lessons.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        LessonCard(parent.context).apply {
            updateLayoutParams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            setEditing(true)
        }
    ).apply {
        card.setOnClickListener { onLessonClickListener?.onLessonClick(lessons[adapterPosition]) }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.card.setLesson(lessons[position])
    }

    class ItemViewHolder(val card: LessonCard) : RecyclerView.ViewHolder(card)
}