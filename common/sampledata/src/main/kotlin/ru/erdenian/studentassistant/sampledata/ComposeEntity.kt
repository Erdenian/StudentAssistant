package ru.erdenian.studentassistant.sampledata

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.ImmutableSortedSet
import ru.erdenian.studentassistant.entity.Lesson
import ru.erdenian.studentassistant.entity.Semester

@SuppressLint("ComposableNaming")
@Composable
fun Semester(
    name: String,
    firstDay: LocalDate,
    lastDay: LocalDate
): Semester = object : Semester, NoOpParcelable {

    override val name = name
    override val firstDay = firstDay
    override val lastDay = lastDay

    override val id get() = error("Not implemented")
}

@SuppressLint("ComposableNaming")
@Composable
fun Lesson(
    subjectName: String,
    type: String,
    teachers: ImmutableSortedSet<String>,
    classrooms: ImmutableSortedSet<String>,
    startTime: LocalTime,
    endTime: LocalTime,
    lessonRepeat: Lesson.Repeat
): Lesson = object : Lesson, NoOpParcelable {

    override val subjectName = subjectName
    override val type = type
    override val teachers = teachers
    override val classrooms = classrooms
    override val startTime = startTime
    override val endTime = endTime
    override val lessonRepeat = lessonRepeat

    override val semesterId get() = error("Not implemented")
    override val id get() = error("Not implemented")
}

@SuppressLint("ComposableNaming")
@Composable
fun ByWeekday(dayOfWeek: DayOfWeek, weeks: List<Boolean>): Lesson.Repeat.ByWeekday =
    object : Lesson.Repeat.ByWeekday(), NoOpParcelable {
        override val dayOfWeek get() = dayOfWeek
        override val weeks get() = weeks
    }

@SuppressLint("ComposableNaming")
@Composable
fun ByDates(dates: Set<LocalDate>): Lesson.Repeat.ByDates = object : Lesson.Repeat.ByDates(), NoOpParcelable {
    override val dates get() = dates
}

@SuppressLint("ComposableNaming")
@Composable
fun Homework(
    subjectName: String,
    description: String,
    deadline: LocalDate,
    isDone: Boolean
): Homework = object : Homework, NoOpParcelable {

    override val subjectName get() = subjectName
    override val description get() = description
    override val deadline get() = deadline
    override val isDone get() = isDone

    override val semesterId get() = error("Not implemented")
    override val id get() = error("Not implemented")
}

private interface NoOpParcelable : Parcelable {
    override fun describeContents() = error("Not implemented")
    override fun writeToParcel(parcel: Parcel, flags: Int) = error("Not implemented")
}
