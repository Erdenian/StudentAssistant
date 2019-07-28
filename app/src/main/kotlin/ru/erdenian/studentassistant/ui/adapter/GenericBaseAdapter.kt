package ru.erdenian.studentassistant.ui.adapter

import android.widget.BaseAdapter

abstract class GenericBaseAdapter<T> : BaseAdapter() {
    abstract override fun getItem(position: Int): T
}
