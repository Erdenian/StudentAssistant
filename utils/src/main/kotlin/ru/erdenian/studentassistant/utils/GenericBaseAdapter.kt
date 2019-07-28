package ru.erdenian.studentassistant.utils

import android.widget.BaseAdapter

abstract class GenericBaseAdapter<T> : BaseAdapter() {
    abstract override fun getItem(position: Int): T
}
