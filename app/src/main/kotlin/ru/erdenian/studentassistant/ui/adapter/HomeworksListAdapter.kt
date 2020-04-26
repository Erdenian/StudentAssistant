package ru.erdenian.studentassistant.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.uikit.HomeworkCard

class HomeworksListAdapter : RecyclerView.Adapter<HomeworksListAdapter.ItemViewHolder>() {

    var homeworks: List<Homework> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onHomeworkClickListener: ((Homework) -> Unit)? = null

    override fun getItemCount() = homeworks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        HomeworkCard(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnLongClickListener { it.showContextMenu() }
        }
    ).apply {
        card.setOnClickListener { onHomeworkClickListener?.invoke(homeworks[adapterPosition]) }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        homeworks[position].run { holder.card.setHomework(subjectName, description, deadline) }
    }

    class ItemViewHolder(val card: HomeworkCard) : RecyclerView.ViewHolder(card)
}
