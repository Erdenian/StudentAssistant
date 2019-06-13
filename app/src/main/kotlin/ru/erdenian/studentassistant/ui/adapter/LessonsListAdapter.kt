package ru.erdenian.studentassistant.ui.adapter

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import ru.erdenian.studentassistant.customviews.LessonCard
import ru.erdenian.studentassistant.repository.entity.LessonNew

class LessonsListAdapter(
    private val isEditing: Boolean = false
) : RecyclerView.Adapter<LessonsListAdapter.ItemViewHolder>() {

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
        }
    ).apply {
        card.setOnClickListener { onLessonClickListener?.onLessonClick(lessons[adapterPosition]) }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.card.setLesson(lessons[position])
    }

    class ItemViewHolder(val card: LessonCard) : RecyclerView.ViewHolder(card)

    private fun LessonCard.setLesson(lesson: LessonNew) = setLesson(
        lesson.subjectName,
        lesson.type,
        lesson.teachers.list,
        lesson.classrooms.list,
        lesson.startTime,
        lesson.endTime
    )
}
