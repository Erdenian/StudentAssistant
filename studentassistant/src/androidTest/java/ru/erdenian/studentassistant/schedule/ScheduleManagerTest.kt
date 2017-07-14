package ru.erdenian.studentassistant.schedule

import android.support.test.InstrumentationRegistry
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.erdenian.studentassistant.extensions.clearApplicationData


class ScheduleManagerTest {

  @Before
  fun before() {
    InstrumentationRegistry.getTargetContext().apply {
      clearApplicationData()
      ScheduleManager.initialize(this)
    }
  }

  @Test
  fun addSemester() {
    val semester = Semester("test", LocalDate.now().minusMonths(5), LocalDate.now())
    ScheduleManager.addSemester(semester)
    val semesters = ScheduleManager.semesters
    assertEquals(1, semesters.size)
    assertEquals(semester, semesters.asList()[0])
  }

  @After
  fun after() {
    InstrumentationRegistry.getTargetContext().clearApplicationData()
  }
}
