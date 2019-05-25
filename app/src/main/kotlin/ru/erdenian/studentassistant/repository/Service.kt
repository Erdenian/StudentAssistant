package ru.erdenian.studentassistant.repository

import java.io.Serializable
import java.util.SortedSet

class ImmutableSortedSet<E : Comparable<E>>(
    private val value: SortedSet<E>
) : Set<E> by value, Serializable {
    val list: SortedList<E> by lazy { value.toSortedList() }
}

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

fun <T : Comparable<T>> immutableSortedSetOf(vararg elements: T) =
    ImmutableSortedSet(elements.toSortedSet())

class SortedList<E : Comparable<E>>(private val value: List<E>) : List<E> by value.sorted()

/**
 * Преобразовывает коллекцию в [SortedList].
 *
 * @author Ilya Solovyev
 * @since 0.3.0
 * @return SortedList, содержащий те же элементы, что и коллекция
 */
fun <E : Comparable<E>> Collection<E>.toSortedList() = SortedList(toList())

/**
 * Преобразовывает [Sequence] в [SortedList].
 *
 * @author Ilya Solovyev
 * @since 0.3.0
 * @return SortedList, содержащий те же элементы, что и коллекция
 */
fun <E : Comparable<E>> Sequence<E>.toSortedList() = SortedList(toList())
