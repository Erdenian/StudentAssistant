package ru.erdenian.studentassistant.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Test

internal class NavigatorTest {

    @Serializable
    private data object RouteStart : NavKey

    @Serializable
    private data object RouteOtherTop : NavKey

    @Serializable
    private data object RouteDeep : NavKey

    @Test
    fun `navigate to same top level route resets stack`() {
        val startStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val otherStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val backStacks: Map<NavKey, NavBackStack<NavKey>> = mapOf(
            RouteStart to startStack,
            RouteOtherTop to otherStack
        )

        // Мокаем поведение списка для .first()
        every { startStack.isEmpty() } returns false
        every { startStack[0] } returns RouteStart

        val state = NavigationState(
            startRoute = RouteStart,
            topLevelRoute = mutableStateOf(RouteStart),
            backStacks = backStacks
        )
        val navigator = Navigator(state)

        navigator.navigate(RouteStart)

        verify { startStack.retainAll(setOf(RouteStart)) }
        assertEquals(RouteStart, state.topLevelRoute)
    }

    @Test
    fun `navigate to other top level route switches tab`() {
        val startStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val otherStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val backStacks: Map<NavKey, NavBackStack<NavKey>> = mapOf(
            RouteStart to startStack,
            RouteOtherTop to otherStack
        )

        val state = NavigationState(
            startRoute = RouteStart,
            topLevelRoute = mutableStateOf(RouteStart),
            backStacks = backStacks
        )
        val navigator = Navigator(state)

        navigator.navigate(RouteOtherTop)

        assertEquals(RouteOtherTop, state.topLevelRoute)
        verify(exactly = 0) { startStack.add(any()) }
        verify(exactly = 0) { otherStack.add(any()) }
    }

    @Test
    fun `navigate to deep route adds to current stack`() {
        val startStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val backStacks: Map<NavKey, NavBackStack<NavKey>> = mapOf(RouteStart to startStack)

        val state = NavigationState(
            startRoute = RouteStart,
            topLevelRoute = mutableStateOf(RouteStart),
            backStacks = backStacks
        )
        val navigator = Navigator(state)

        navigator.navigate(RouteDeep)

        verify { startStack.add(RouteDeep) }
        assertEquals(RouteStart, state.topLevelRoute)
    }

    @Test
    fun `goBack from deep route removes from stack`() {
        val startStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val backStacks: Map<NavKey, NavBackStack<NavKey>> = mapOf(RouteStart to startStack)

        // Мокаем поведение списка для .last() и .removeLastOrNull()
        // last() использует get(size - 1)
        // removeLastOrNull() использует removeAt(size - 1)
        every { startStack.isEmpty() } returns false
        every { startStack.size } returns 2
        every { startStack[1] } returns RouteDeep
        every { startStack.removeAt(1) } returns RouteDeep

        val state = NavigationState(
            startRoute = RouteStart,
            topLevelRoute = mutableStateOf(RouteStart),
            backStacks = backStacks
        )
        val navigator = Navigator(state)

        navigator.goBack()

        verify { startStack.removeAt(1) }
        assertEquals(RouteStart, state.topLevelRoute)
    }

    @Test
    fun `goBack from root of top level route switches to start route`() {
        val startStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val otherStack = mockk<NavBackStack<NavKey>>(relaxed = true)
        val backStacks: Map<NavKey, NavBackStack<NavKey>> = mapOf(
            RouteStart to startStack,
            RouteOtherTop to otherStack
        )

        // Мокаем поведение списка для .last()
        every { otherStack.isEmpty() } returns false
        every { otherStack.size } returns 1
        every { otherStack[0] } returns RouteOtherTop

        val state = NavigationState(
            startRoute = RouteStart,
            topLevelRoute = mutableStateOf(RouteOtherTop),
            backStacks = backStacks
        )
        val navigator = Navigator(state)

        navigator.goBack()

        verify(exactly = 0) { otherStack.removeAt(any()) }
        assertEquals(RouteStart, state.topLevelRoute)
    }

    @Test
    fun `stacksInUse returns correct list`() {
        val startStack = mockk<NavBackStack<NavKey>>()
        val otherStack = mockk<NavBackStack<NavKey>>()
        val backStacks: Map<NavKey, NavBackStack<NavKey>> = mapOf(
            RouteStart to startStack,
            RouteOtherTop to otherStack
        )

        val state = NavigationState(
            startRoute = RouteStart,
            topLevelRoute = mutableStateOf(RouteStart),
            backStacks = backStacks
        )

        assertEquals(listOf(RouteStart), state.stacksInUse)

        state.topLevelRoute = RouteOtherTop
        assertEquals(listOf(RouteStart, RouteOtherTop), state.stacksInUse)
    }
}
