package com.erdenian.studentassistant.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class ImmutableSortedSetTest {

    @Test
    fun equalsTest() {
        immutableSortedSetOf(1, 2, 3).let { assertEquals(it, it) }
        assertNotEquals(immutableSortedSetOf(1, 2, 3), sortedSetOf(1, 2, 3))
        assertNotEquals(immutableSortedSetOf(1, 2, 3), setOf(1, 2, 3))
        assertNotEquals(immutableSortedSetOf(1, 2, 3), immutableSortedSetOf(1, 2))
        assertNotEquals(immutableSortedSetOf(1, 2, 3), immutableSortedSetOf(1, 2, 3, 4))
        assertEquals(immutableSortedSetOf(1, 2, 3), immutableSortedSetOf(1, 2, 3))
        assertEquals(immutableSortedSetOf(1, 2, 3), immutableSortedSetOf(2, 1, 3))
        assertEquals(immutableSortedSetOf(1, 2, 3), immutableSortedSetOf(1, 1, 3, 3, 2, 2))
    }

    @Test
    fun hashCodeTest() {
        class Test(val hashCode: Int) : Comparable<Test> {
            override fun compareTo(other: Test) = this.hashCode.compareTo(other.hashCode)
            override fun hashCode() = hashCode
        }

        assertEquals(0, emptyImmutableSortedSet<Test>().hashCode())
        assertEquals(0, immutableSortedSetOf(Test(0)).hashCode())
        assertEquals(2, immutableSortedSetOf(Test(2)).hashCode())
        assertEquals(2, immutableSortedSetOf(Test(2), Test(2)).hashCode())
        assertEquals(6, immutableSortedSetOf(Test(2), Test(4)).hashCode())
        assertEquals(6, immutableSortedSetOf(Test(4), Test(2)).hashCode())
        assertEquals(-2147483645, immutableSortedSetOf(Test(Int.MAX_VALUE), Test(4)).hashCode())
    }

    @Test
    fun toStringTest() {
        class Test(val string: String) : Comparable<Test> {
            override fun compareTo(other: Test) = this.string.compareTo(other.string)
            override fun toString() = string
        }

        assertEquals("[]", emptyImmutableSortedSet<Test>().toString())
        assertEquals("[element1]", immutableSortedSetOf(Test("element1")).toString())
        assertEquals("[element1]", immutableSortedSetOf(Test("element1"), Test("element1")).toString())
        assertEquals("[element1, element2]", immutableSortedSetOf(Test("element1"), Test("element2")).toString())
        assertEquals("[element1, element2]", immutableSortedSetOf(Test("element2"), Test("element1")).toString())
    }

    @Test
    fun listTest() {
        assertEquals(emptyList<Int>(), emptyImmutableSortedSet<Int>().list)
        assertEquals(listOf(1), immutableSortedSetOf(1).list)
        assertEquals(listOf(1), immutableSortedSetOf(1, 1).list)
        assertEquals(listOf(1, 2), immutableSortedSetOf(1, 2).list)
        assertEquals(listOf(1, 2), immutableSortedSetOf(2, 1).list)

        emptyImmutableSortedSet<Int>().let { assertSame(it.list, it.list) }
        assertSame(emptyImmutableSortedSet<Int>().list, emptyImmutableSortedSet<Int>().list)
        immutableSortedSetOf(1, 2).let { assertSame(it.list, it.list) }
        assertNotSame(immutableSortedSetOf(1, 2).list, immutableSortedSetOf(1, 2).list)
    }
}
