package ru.erdenian.studentassistant.extensions

import android.widget.ListView

var ListView.scrollPosition: Pair<Int, Int>
    get() = Pair(firstVisiblePosition, (getChildAt(0)?.top ?: paddingTop) - paddingTop)
    set(value) = setSelectionFromTop(value.first, value.second)