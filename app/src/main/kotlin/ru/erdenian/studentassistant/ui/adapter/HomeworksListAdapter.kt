package ru.erdenian.studentassistant.ui.adapter

import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.uikit.views.HomeworkCard

class HomeworksListAdapter : RecyclerView.Adapter<HomeworksListAdapter.ItemViewHolder>() {

    var homeworks: List<Homework> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onHomeworkClickListener: ((Homework) -> Unit)? = null

    private val deadlineFormatter = DateTimeFormat.shortDate()

    override fun getItemCount() = homeworks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(ComposeView(parent.context))

    @OptIn(ExperimentalFoundationApi::class)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val homework = homeworks[position]
        holder.view.setContent {
            MaterialTheme {
                HomeworkCard(
                    homework.subjectName,
                    homework.description,
                    homework.deadline.toString(deadlineFormatter),
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = { holder.view.showContextMenu() },
                            onClick = { onHomeworkClickListener?.invoke(homework) }
                        )
                )
            }
        }
    }

    class ItemViewHolder(val view: ComposeView) : RecyclerView.ViewHolder(view)
}
