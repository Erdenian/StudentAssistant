package ru.erdenian.studentassistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.localdata.entity.SemesterNew

class SemestersAdapter(context: Context, private val semesters: List<SemesterNew>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.spinner_item_semesters, parent, false)
        view as TextView
        view.text = semesters[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(
            R.layout.spinner_dropdown_item_semesters,
            parent, false
        )
        view as TextView
        view.text = semesters[position].name
        return view
    }

    override fun getItem(position: Int) = semesters[position]

    override fun getItemId(position: Int) = semesters[position].id

    override fun getCount() = semesters.size
}