package ru.erdenian.studentassistant.ui.adapter

import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.views.LessonCard

class LessonsListAdapter : RecyclerView.Adapter<LessonsListAdapter.ItemViewHolder>() {

    var lessons: List<Lesson> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onLessonClickListener: ((Lesson) -> Unit)? = null

    private val timeFormatter = DateTimeFormat.shortTime()

    override fun getItemCount() = lessons.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(ComposeView(parent.context))

    @OptIn(ExperimentalFoundationApi::class)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.view.setContent {
            AppTheme {
                LessonCard(
                    subjectName = lesson.subjectName,
                    type = lesson.type,
                    teachers = lesson.teachers.list,
                    classrooms = lesson.classrooms.list,
                    startTime = lesson.startTime.toString(timeFormatter),
                    endTime = lesson.endTime.toString(timeFormatter),
                    onClick = { onLessonClickListener?.invoke(lesson) },
                    onLongClick = { holder.view.showContextMenu() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    class ItemViewHolder(val view: ComposeView) : RecyclerView.ViewHolder(view)
}
