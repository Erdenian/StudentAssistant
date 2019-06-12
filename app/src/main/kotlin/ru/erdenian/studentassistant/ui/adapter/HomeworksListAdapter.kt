package ru.erdenian.studentassistant.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.repository.entity.HomeworkNew

class HomeworksListAdapter : RecyclerView.Adapter<HomeworksListAdapter.ItemViewHolder>() {

    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }

    var homeworks: List<HomeworkNew> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnHomeworkClickListener {
        fun onHomeworkClick(homework: HomeworkNew)
    }

    var onHomeworkClickListener: OnHomeworkClickListener? = null

    override fun getItemCount() = homeworks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.card_homework, parent, false
        )
    ).apply {
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
        val subjectName: TextView = itemView.findViewById(R.id.ch_subject_name)
        val description: TextView = itemView.findViewById(R.id.ch_description)
        val deadline: TextView = itemView.findViewById(R.id.ch_deadline)
    }
}
