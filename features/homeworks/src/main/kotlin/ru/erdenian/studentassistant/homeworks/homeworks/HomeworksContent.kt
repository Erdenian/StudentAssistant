package ru.erdenian.studentassistant.homeworks.homeworks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.erdenian.studentassistant.homeworks.composable.LazyHomeworksList
import ru.erdenian.studentassistant.repository.api.entity.Homework
import ru.erdenian.studentassistant.repository.api.entity.Semester
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.ScreenPreviews
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.layout.ContextMenuBox
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu

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
    onDeleteHomeworkClick: (Homework) -> Unit,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if ((selectedSemester == null) || (semesters.size <= 1)) {
                        Text(text = stringResource(RS.h_title))
                    } else {
                        TopAppBarDropdownMenu(
                            items = semesters,
                            selectedItem = selectedSemester.name,
                            onSelectedItemChange = { index, _ -> onSelectedSemesterChange(index) },
                        )
                    }
                },
                actions = {
                    TopAppBarActions(
                        actions = listOfNotNull(
                            selectedSemester?.let { semester ->
                                ActionItem.AlwaysShow(
                                    name = stringResource(RS.h_add),
                                    imageVector = AppIcons.Add,
                                    onClick = { onAddHomeworkClick(semester) },
                                )
                            },
                        ),
                    )
                },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            if (selectedSemester == null) {
                Text(
                    text = stringResource(RS.h_no_schedule),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.screenPaddingHorizontal),
                )
            } else {
                var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }

                ContextMenuBox(
                    expanded = (contextMenuHomework != null),
                    onDismissRequest = { contextMenuHomework = null },
                    contextMenu = {
                        DropdownMenuItem(
                            text = { Text(stringResource(RS.h_delete_homework)) },
                            onClick = {
                                val homework = checkNotNull(contextMenuHomework)
                                contextMenuHomework = null
                                onDeleteHomeworkClick(homework)
                            },
                        )
                    },
                ) {
                    LazyHomeworksList(
                        overdueHomeworks = overdueHomeworks,
                        actualHomeworks = actualHomeworks,
                        pastHomeworks = pastHomeworks,
                        onHomeworkClick = onHomeworkClick,
                        onLongHomeworkClick = { contextMenuHomework = it },
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                    )
                }
            }
        }
    }
}

private data class HomeworksContentPreviewData(
    val semesters: List<String>,
    val selectedSemester: Semester?,
    val overdue: List<Homework>?,
    val actual: List<Homework>?,
    val past: List<Homework>?,
)

private class HomeworksContentPreviewParameterProvider : PreviewParameterProvider<HomeworksContentPreviewData> {
    override val values = sequenceOf(
        HomeworksContentPreviewData(
            semesters = emptyList(),
            selectedSemester = null,
            overdue = emptyList(),
            actual = emptyList(),
            past = emptyList(),
        ),
        HomeworksContentPreviewData(
            semesters = listOf(Semesters.regular.name),
            selectedSemester = Semesters.regular,
            overdue = null,
            actual = null,
            past = null,
        ),
        HomeworksContentPreviewData(
            semesters = listOf(Semesters.regular.name),
            selectedSemester = Semesters.regular,
            overdue = emptyList(),
            actual = emptyList(),
            past = emptyList(),
        ),
        HomeworksContentPreviewData(
            semesters = listOf(Semesters.regular.name),
            selectedSemester = Semesters.regular,
            overdue = List(3) { Homeworks.regular },
            actual = List(3) { Homeworks.regular },
            past = List(3) { Homeworks.regular },
        ),
    )
}

@ScreenPreviews
@Composable
private fun HomeworksContentPreview(
    @PreviewParameter(HomeworksContentPreviewParameterProvider::class) data: HomeworksContentPreviewData,
) = AppTheme {
    HomeworksContent(
        semesters = data.semesters,
        selectedSemester = data.selectedSemester,
        overdueHomeworks = data.overdue,
        actualHomeworks = data.actual,
        pastHomeworks = data.past,
        onSelectedSemesterChange = {},
        onAddHomeworkClick = {},
        onHomeworkClick = {},
        onDeleteHomeworkClick = {},
    )
}
