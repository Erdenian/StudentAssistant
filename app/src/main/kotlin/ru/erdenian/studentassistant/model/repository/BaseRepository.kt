package ru.erdenian.studentassistant.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.shopify.livedataktx.toKtx
import ru.erdenian.studentassistant.entity.toImmutableSortedSet

@Suppress("UnnecessaryAbstractClass")
abstract class BaseRepository {

    protected fun <T : Comparable<T>> List<T>.map() = toImmutableSortedSet()
    protected fun <T : Comparable<T>> LiveData<List<T>>.map() =
        map { it.toImmutableSortedSet() }.toKtx()
}
