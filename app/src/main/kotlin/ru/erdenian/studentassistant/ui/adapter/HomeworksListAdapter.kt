package ru.erdenian.studentassistant.ui.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.model.entity.Homework
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class HomeworksListAdapter : RecyclerView.Adapter<HomeworksListAdapter.ItemViewHolder>() {

    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    var homeworks: List<Homework> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnHomeworkClickListener {
        fun onHomeworkClick(homework: Homework)
    }

    var onHomeworkClickListener: OnHomeworkClickListener? = null

    override fun getItemCount() = homeworks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.card_homework, parent, false
        )
    ).apply {
        TypedValue().also { outValue ->
            parent.context.theme.resolveAttribute(
                android.R.attr.selectableItemBackground, outValue, true
            )
            (itemView as CardView).foreground = parent.context.getDrawable(outValue.resourceId)
        }
        itemView.setOnClickListener {
            onHomeworkClickListener?.onHomeworkClick(homeworks[adapterPosition])
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        homeworks[position].run {
            holder.subjectName.text = subjectName
            holder.description.text = description
            holder.deadline.text = deadline.toString(DATE_FORMAT)
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.requireViewByIdCompat(R.id.ch_subject_name)
        val description: TextView = itemView.requireViewByIdCompat(R.id.ch_description)
        val deadline: TextView = itemView.requireViewByIdCompat(R.id.ch_deadline)
    }
}
