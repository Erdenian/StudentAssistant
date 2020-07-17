package ru.erdenian.studentassistant.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.utils.GenericBaseAdapter

class SemestersSpinnerAdapter : GenericBaseAdapter<Semester>() {

    var semesters: List<Semester> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    @Suppress("UnsafeCast")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        getOrInflateView<TextView>(convertView, parent, R.layout.spinner_item_semesters).apply {
            text = semesters[position].name
        }

    @Suppress("UnsafeCast")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        getOrInflateView<TextView>(convertView, parent, R.layout.spinner_dropdown_item_semesters).apply {
            text = semesters[position].name
        }

    override fun getItem(position: Int) = semesters[position]
    override fun getItemId(position: Int) = semesters[position].id
    override fun getCount() = semesters.size
}
