package ru.erdenian.studentassistant.homeworks.homeworks

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.entity.Semester
import ru.erdenian.studentassistant.homeworks.composable.LazyHomeworksList
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.ContextMenuDialog
import ru.erdenian.studentassistant.uikit.view.ContextMenuItem
import ru.erdenian.studentassistant.uikit.view.ProgressDialog
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu

@Composable
fun HomeworksScreen(
    viewModel: HomeworksViewModel,
    navigateToCreateHomework: (semesterId: Long) -> Unit,
    navigateToEditHomework: (semesterId: Long, homeworkId: Long) -> Unit
) {
    val operation by viewModel.operation.collectAsState()

    val semesters by viewModel.allSemesters.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    val overdueHomeworks by viewModel.overdue.collectAsState()
    val actualHomeworks by viewModel.actual.collectAsState()
    val pastHomeworks by viewModel.past.collectAsState()

    HomeworksContent(
        operation = operation,
        semesters = semesters.map { it.name },
        selectedSemester = selectedSemester,
        overdueHomeworks = overdueHomeworks?.list,
        actualHomeworks = actualHomeworks?.list,
        pastHomeworks = pastHomeworks?.list,
        onSelectedSemesterChange = { viewModel.selectSemester(semesters.list[it].id) },
        onAddHomeworkClick = { navigateToCreateHomework(it.id) },
        onHomeworkClick = { navigateToEditHomework(it.semesterId, it.id) },
        onDeleteHomeworkClick = { viewModel.deleteHomework(it.id) }
    )
}

@Composable
private fun HomeworksContent(
    operation: HomeworksViewModel.Operation?,
    semesters: List<String>,
    selectedSemester: Semester?,
    overdueHomeworks: List<Homework>?,
    actualHomeworks: List<Homework>?,
    pastHomeworks: List<Homework>?,
    onSelectedSemesterChange: (Int) -> Unit,
    onAddHomeworkClick: (Semester) -> Unit,
    onHomeworkClick: (Homework) -> Unit,
    onDeleteHomeworkClick: (Homework) -> Unit
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
    if (operation != null) {
        val stringId = when (operation) {
            HomeworksViewModel.Operation.DELETING_HOMEWORK -> RS.h_delete_progress
        }
        ProgressDialog { Text(text = stringResource(stringId)) }
    }

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
            var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }

            LazyHomeworksList(
                overdueHomeworks = overdueHomeworks,
                actualHomeworks = actualHomeworks,
                pastHomeworks = pastHomeworks,
                onHomeworkClick = onHomeworkClick,
                onLongHomeworkClick = { contextMenuHomework = it }
            )

            contextMenuHomework?.let { homework ->
                val context = LocalContext.current
                ContextMenuDialog(
                    onDismissRequest = { contextMenuHomework = null },
                    title = homework.subjectName,
                    items = listOf(
                        ContextMenuItem(stringResource(RS.h_delete_homework)) {
                            contextMenuHomework = null
                            MaterialAlertDialogBuilder(context)
                                .setMessage(RS.h_delete_message)
                                .setPositiveButton(RS.h_delete_yes) { _, _ -> onDeleteHomeworkClick(homework) }
                                .setNegativeButton(RS.h_delete_no, null)
                                .show()
                        }
                    )
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworksContentRegularPreview() = AppTheme {
    HomeworksContent(
        operation = null,
        semesters = listOf(Semesters.regular.name),
        selectedSemester = Semesters.regular,
        overdueHomeworks = List(3) { Homeworks.regular },
        actualHomeworks = List(3) { Homeworks.regular },
        pastHomeworks = List(3) { Homeworks.regular },
        onSelectedSemesterChange = {},
        onAddHomeworkClick = {},
        onHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}
