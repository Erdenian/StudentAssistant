package ru.erdenian.studentassistant.extensions

import com.google.common.collect.ImmutableSortedSet

/**
 * Преобразовывает коллекцию в [ImmutableSortedSet].
 *
 * @author Ilya Solovyev
 * @since 0.2.6
 * @return ImmutableSortedSet, содержащий те же элементы, что и коллекция
 */
fun <E> Collection<E>.toImmutableSortedSet(): ImmutableSortedSet<E> =
    ImmutableSortedSet.copyOf(this)
