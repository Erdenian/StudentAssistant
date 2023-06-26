package com.erdenian.studentassistant.entity

import java.util.SortedSet

/**
 * Неизменяемый отсортированный набор объектов.
 *
 * @author Ilya Solovyov
 * @since 0.3.0
 */
class ImmutableSortedSet<E : Comparable<E>>(value: SortedSet<E>) : Set<E> by value {

    val list: List<E> by lazy { value.toList() }

    override fun equals(other: Any?) = when {
        (other === this) -> true
        (other !is ImmutableSortedSet<*>) -> false
        (this.size != other.size) -> false
        else -> this.containsAll(other)
    }

    override fun hashCode() = sumOf { it.hashCode() }

    override fun toString() = joinToString(", ", "[", "]")
}

/**
 * Преобразовывает коллекцию в [ImmutableSortedSet].
 *
 * @return ImmutableSortedSet, содержащий те же элементы, что и коллекция
 * @author Ilya Solovyov
 * @since 0.2.6
 */
fun <E : Comparable<E>> Iterable<E>.toImmutableSortedSet() = ImmutableSortedSet(toSortedSet())

/**
 * Преобразовывает [Sequence] в [ImmutableSortedSet].
 *
 * @return ImmutableSortedSet, содержащий те же элементы, что и коллекция
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <E : Comparable<E>> Sequence<E>.toImmutableSortedSet() = ImmutableSortedSet(toSortedSet())

fun <T : Comparable<T>> emptyImmutableSortedSet() = ImmutableSortedSet(sortedSetOf<T>())
fun <T : Comparable<T>> immutableSortedSetOf(vararg elements: T) = ImmutableSortedSet(elements.toSortedSet())

fun <T : Comparable<T>> immutableSortedSetOfNotNull(vararg elements: T?) =
    ImmutableSortedSet(elements.asSequence().filterNotNull().toSortedSet())
