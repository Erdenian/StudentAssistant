package ru.erdenian.studentassistant.extensions

import com.google.common.collect.ImmutableSortedSet
import java.util.*

fun <E : Any> ImmutableSortedSet<E>.addToNewSet(element: E): ImmutableSortedSet<E> {
    val set = TreeSet(this)
    set.add(element)
    return ImmutableSortedSet.copyOf(set)
}

fun <E : Any> ImmutableSortedSet<E>.replaceToNewSet(e1: E, e2: E): ImmutableSortedSet<E> {
    val set = TreeSet(this)
    set.remove(e1)
    set.add(e2)
    return ImmutableSortedSet.copyOf(set)
}