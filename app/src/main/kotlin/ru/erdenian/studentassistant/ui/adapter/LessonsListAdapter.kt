package ru.erdenian.studentassistant.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.erdenian.studentassistant.customviews.LessonCard
import ru.erdenian.studentassistant.entity.Lesson

class LessonsListAdapter : RecyclerView.Adapter<LessonsListAdapter.ItemViewHolder>() {

    var lessons: List<Lesson> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnLessonClickListener {
        fun onLessonClick(lesson: Lesson)
    }

    var onLessonClickListener: OnLessonClickListener? = null

    override fun getItemCount() = lessons.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        LessonCard(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnLongClickListener { it.showContextMenu() }
        }
    ).apply {
        card.setOnClickListener { onLessonClickListener?.onLessonClick(lessons[adapterPosition]) }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.card.setLesson(lessons[position])
    }

    class ItemViewHolder(val card: LessonCard) : RecyclerView.ViewHolder(card)

    private fun LessonCard.setLesson(lesson: Lesson) = setLesson(
        lesson.subjectName,
        lesson.type,
        lesson.teachers.list,
        lesson.classrooms.list,
        lesson.startTime,
        lesson.endTime
    )
}
