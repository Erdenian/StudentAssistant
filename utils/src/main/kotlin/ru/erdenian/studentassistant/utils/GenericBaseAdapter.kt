package ru.erdenian.studentassistant.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes

abstract class GenericBaseAdapter<T> : BaseAdapter() {

    abstract override fun getItem(position: Int): T

    protected inline fun <reified T : View> getOrInflateView(convertView: View?, parent: ViewGroup, @LayoutRes resource: Int): T {
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(resource, parent, false)
        return view as T
    }
}
