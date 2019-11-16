package ru.erdenian.studentassistant.entity

import android.os.Parcel
import android.os.Parcelable
import java.util.SortedSet

/**
 * Неизменяемый отсортированный набор объектов
 *
 * @author Ilya Solovyov
 * @since 0.3.0
 */
class ImmutableSortedSet<E : Comparable<E>>(
    private val value: SortedSet<E>
) : Set<E> by value, Parcelable {

    companion object CREATOR : Parcelable.Creator<ImmutableSortedSet<Comparable<Any>>> {
        override fun createFromParcel(parcel: Parcel) = ImmutableSortedSet<Comparable<Any>>(parcel)
        override fun newArray(size: Int) = arrayOfNulls<ImmutableSortedSet<Comparable<Any>>?>(size)
    }

    @Suppress("UNCHECKED_CAST")
    private constructor(parcel: Parcel) : this(
        (parcel.readArrayList(
            ImmutableSortedSet<E>::value.javaClass.classLoader
        ) as List<E>).toSortedSet()
    )

    val list: SortedList<E> by lazy { value.toSortedList() }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(list)
    }

    override fun describeContents() = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImmutableSortedSet<*>
        if (value != other.value) return false
        return true
    }

    override fun hashCode() = value.hashCode()
}

/**
 * Преобразовывает коллекцию в [ImmutableSortedSet]
 *
 * @return ImmutableSortedSet, содержащий те же элементы, что и коллекция
 * @author Ilya Solovyov
 * @since 0.2.6
 */
fun <E : Comparable<E>> Collection<E>.toImmutableSortedSet() = ImmutableSortedSet(toSortedSet())

/**
 * Преобразовывает [Sequence] в [ImmutableSortedSet]
 *
 * @return ImmutableSortedSet, содержащий те же элементы, что и коллекция
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <E : Comparable<E>> Sequence<E>.toImmutableSortedSet() = ImmutableSortedSet(toSortedSet())

fun <T : Comparable<T>> immutableSortedSetOf(vararg elements: T) =
    ImmutableSortedSet(elements.toSortedSet())

class SortedList<E : Comparable<E>>(private val value: List<E>) : List<E> by value.sorted()

/**
 * Преобразовывает коллекцию в [SortedList]
 *
 * @return SortedList, содержащий те же элементы, что и коллекция
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <E : Comparable<E>> Collection<E>.toSortedList() = SortedList(toList())

/**
 * Преобразовывает [Sequence] в [SortedList]
 *
 * @return SortedList, содержащий те же элементы, что и коллекция
 * @author Ilya Solovyov
 * @since 0.3.0
 */
fun <E : Comparable<E>> Sequence<E>.toSortedList() = SortedList(toList())
