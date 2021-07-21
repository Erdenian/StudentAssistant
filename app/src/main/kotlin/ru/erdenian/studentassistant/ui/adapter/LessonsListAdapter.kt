package ru.erdenian.studentassistant.ui.adapter

import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
                    lesson.subjectName,
                    lesson.type,
                    lesson.teachers.list,
                    lesson.classrooms.list,
                    lesson.startTime.toString(timeFormatter),
                    lesson.endTime.toString(timeFormatter),
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = { holder.view.showContextMenu() },
                            onClick = { onLessonClickListener?.invoke(lesson) }
                        )
                )
            }
        }
    }

    class ItemViewHolder(val view: ComposeView) : RecyclerView.ViewHolder(view)
}
