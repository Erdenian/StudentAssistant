package com.erdenian.studentassistant.homeworks.homeworks

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.erdenian.studentassistant.entity.Homework
import com.erdenian.studentassistant.entity.Semester
import com.erdenian.studentassistant.homeworks.composable.LazyHomeworksList
import com.erdenian.studentassistant.sampledata.Homeworks
import com.erdenian.studentassistant.sampledata.Semesters
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions
import com.erdenian.studentassistant.uikit.view.ActionItem
import com.erdenian.studentassistant.uikit.view.TopAppBarActions
import com.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu

@Composable
internal fun HomeworksContent(
    semesters: List<String>,
    selectedSemester: Semester?,
    overdueHomeworks: List<Homework>?,
    actualHomeworks: List<Homework>?,
    pastHomeworks: List<Homework>?,
    onSelectedSemesterChange: (Int) -> Unit,
    onAddHomeworkClick: (Semester) -> Unit,
    onHomeworkClick: (Homework) -> Unit,
    onLongHomeworkClick: (Homework) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = {
                if ((selectedSemester == null) || (semesters.size <= 1)) {
                    Text(text = stringResource(RS.h_title))
                } else {
                    TopAppBarDropdownMenu(
                        items = semesters,
                        selectedItem = selectedSemester.name,
                        onSelectedItemChange = { index, _ -> onSelectedSemesterChange(index) }
                    )
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOfNotNull(
                        if (selectedSemester != null) {
                            ActionItem.AlwaysShow(
                                name = stringResource(RS.h_add),
                                imageVector = AppIcons.Add,
                                onClick = { onAddHomeworkClick(selectedSemester) }
                            )
                        } else null
                    )
                )
            }
        )
    }
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (selectedSemester == null) {
            Text(
                text = stringResource(RS.h_no_schedule),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
            )
        } else {
            LazyHomeworksList(
                overdueHomeworks = overdueHomeworks,
                actualHomeworks = actualHomeworks,
                pastHomeworks = pastHomeworks,
                onHomeworkClick = onHomeworkClick,
                onLongHomeworkClick = onLongHomeworkClick
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworksContentNoSchedulePreview() = AppTheme {
    HomeworksContent(
        semesters = emptyList(),
        selectedSemester = null,
        overdueHomeworks = emptyList(),
        actualHomeworks = emptyList(),
        pastHomeworks = emptyList(),
        onSelectedSemesterChange = {},
        onAddHomeworkClick = {},
        onHomeworkClick = {},
        onLongHomeworkClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworksContentLoadingPreview() = AppTheme {
    HomeworksContent(
        semesters = listOf(Semesters.regular.name),
        selectedSemester = Semesters.regular,
        overdueHomeworks = null,
        actualHomeworks = null,
        pastHomeworks = null,
        onSelectedSemesterChange = {},
        onAddHomeworkClick = {},
        onHomeworkClick = {},
        onLongHomeworkClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworksContentNoHomeworksPreview() = AppTheme {
    HomeworksContent(
        semesters = listOf(Semesters.regular.name),
        selectedSemester = Semesters.regular,
        overdueHomeworks = emptyList(),
        actualHomeworks = emptyList(),
        pastHomeworks = emptyList(),
        onSelectedSemesterChange = {},
        onAddHomeworkClick = {},
        onHomeworkClick = {},
        onLongHomeworkClick = {}
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworksContentRegularPreview() = AppTheme {
    HomeworksContent(
        semesters = listOf(Semesters.regular.name),
        selectedSemester = Semesters.regular,
        overdueHomeworks = List(3) { Homeworks.regular },
        actualHomeworks = List(3) { Homeworks.regular },
        pastHomeworks = List(3) { Homeworks.regular },
        onSelectedSemesterChange = {},
        onAddHomeworkClick = {},
        onHomeworkClick = {},
        onLongHomeworkClick = {}
    )
}
