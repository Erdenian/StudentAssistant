package ru.erdenian.studentassistant.repository

import java.io.Serializable
import java.util.SortedSet

class ImmutableSortedSet<E>(private val value: SortedSet<E>) : Set<E> by value, Serializable

/**
 * Преобразовывает коллекцию в [ImmutableSortedSet].
 *
 * @author Ilya Solovyev
 * @since 0.2.6
 * @return ImmutableSortedSet, содержащий те же элементы, что и коллекция
 */
fun <E : Comparable<E>> Collection<E>.toImmutableSortedSet() = ImmutableSortedSet(toSortedSet())

/**
 * Преобразовывает [Sequence] в [ImmutableSortedSet].
 *
 * @author Ilya Solovyev
 * @since 0.3.0
 * @return ImmutableSortedSet, содержащий те же элементы, что и коллекция
 */
fun <E : Comparable<E>> Sequence<E>.toImmutableSortedSet() = ImmutableSortedSet(toSortedSet())
