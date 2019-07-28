package ru.erdenian.studentassistant.model

import android.os.Parcel
import android.os.Parcelable
import java.util.SortedSet

data class ImmutableSortedSet<E : Comparable<E>>(
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
