package ru.erdenian.studentassistant.extensions

import com.google.common.collect.ImmutableSortedSet

fun <E> Collection<E>.toImmutableSortedSet(): ImmutableSortedSet<E> = ImmutableSortedSet.copyOf(this)
