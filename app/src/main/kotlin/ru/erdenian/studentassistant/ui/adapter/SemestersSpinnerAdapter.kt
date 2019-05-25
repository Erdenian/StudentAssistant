package ru.erdenian.studentassistant.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.repository.entity.SemesterNew

class SemestersSpinnerAdapter : BaseAdapter() {

    var semesters: List<SemesterNew> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        ((convertView ?: LayoutInflater.from(parent.context).inflate(
            R.layout.spinner_item_semesters, parent, false
        )) as TextView).apply { text = semesters[position].name }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        ((convertView ?: LayoutInflater.from(parent.context).inflate(
            R.layout.spinner_dropdown_item_semesters, parent, false
        )) as TextView).apply { text = semesters[position].name }

    override fun getItem(position: Int) = semesters[position]
    override fun getItemId(position: Int) = semesters[position].id
    override fun getCount() = semesters.size
}
