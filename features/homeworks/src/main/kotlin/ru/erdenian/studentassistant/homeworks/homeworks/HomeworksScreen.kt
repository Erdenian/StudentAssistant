package ru.erdenian.studentassistant.homeworks.homeworks

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.homeworks.R
import ru.erdenian.studentassistant.sampledata.Homeworks
import ru.erdenian.studentassistant.sampledata.Semesters
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme
import ru.erdenian.studentassistant.style.dimensions
import ru.erdenian.studentassistant.uikit.view.ActionItem
import ru.erdenian.studentassistant.uikit.view.HomeworkCard
import ru.erdenian.studentassistant.uikit.view.TopAppBarActions
import ru.erdenian.studentassistant.uikit.view.TopAppBarDropdownMenu

@Composable
fun HomeworksScreen(
    viewModel: HomeworksViewModel,
    navigateToCreateHomework: (semesterId: Long) -> Unit,
    navigateToEditHomework: (semesterId: Long, homeworkId: Long) -> Unit
) {
    val semesters by viewModel.allSemesters.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()

    val overdueHomeworks by viewModel.overdue.collectAsState()
    val actualHomeworks by viewModel.actual.collectAsState()
    val pastHomeworks by viewModel.past.collectAsState()

    val context = LocalContext.current

    HomeworksContent(
        semesters = semesters.map { it.name },
        selectedSemester = selectedSemester?.name,
        overdueHomeworks = overdueHomeworks.list,
        actualHomeworks = actualHomeworks.list,
        pastHomeworks = pastHomeworks.list,
        onSelectedSemesterChange = { viewModel.selectSemester(semesters.list[it].id) },
        onAddHomeworkClick = { navigateToCreateHomework(checkNotNull(selectedSemester).id) },
        onHomeworkClick = { navigateToEditHomework(checkNotNull(selectedSemester).id, it.id) },
        onDeleteHomeworkClick = { homework ->
            MaterialAlertDialogBuilder(context)
                .setMessage(R.string.h_delete_message)
                .setPositiveButton(R.string.h_delete_yes) { _, _ -> viewModel.deleteHomework(homework.id) }
                .setNegativeButton(R.string.h_delete_no, null)
                .show()
        }
    )
}

@Composable
private fun HomeworksContent(
    semesters: List<String>,
    selectedSemester: String?,
    overdueHomeworks: List<Homework>,
    actualHomeworks: List<Homework>,
    pastHomeworks: List<Homework>,
    onSelectedSemesterChange: (Int) -> Unit,
    onAddHomeworkClick: () -> Unit,
    onHomeworkClick: (Homework) -> Unit,
    onDeleteHomeworkClick: (Homework) -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = {
                if (semesters.size <= 1) {
                    Text(text = stringResource(R.string.h_title))
                } else {
                    TopAppBarDropdownMenu(
                        items = semesters,
                        selectedItem = checkNotNull(selectedSemester),
                        onSelectedItemChange = { index, _ -> onSelectedSemesterChange(index) }
                    )
                }
            },
            actions = {
                TopAppBarActions(
                    actions = listOfNotNull(
                        if (semesters.isNotEmpty()) {
                            ActionItem.AlwaysShow(
                                name = stringResource(R.string.h_add),
                                imageVector = AppIcons.Add,
                                onClick = onAddHomeworkClick
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
        when {
            semesters.isEmpty() -> Text(
                text = stringResource(R.string.h_no_schedule),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
            )
            overdueHomeworks.isEmpty() && actualHomeworks.isEmpty() && pastHomeworks.isEmpty() -> Text(
                text = stringResource(R.string.h_no_homeworks),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.activityHorizontalMargin)
            )
            else -> {
                var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }
                val deadlineFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }

                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.dimensions.activityHorizontalMargin,
                        vertical = MaterialTheme.dimensions.activityVerticalMargin
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.cardsSpacing),
                    modifier = Modifier.fillMaxSize()
                ) {
                    fun LazyListScope.createList(homeworks: List<Homework>) = itemsIndexed(
                        items = homeworks,
                        key = { _, item -> item.id }
                    ) { _, homework ->
                        HomeworkCard(
                            subjectName = homework.subjectName,
                            description = homework.description,
                            deadline = homework.deadline.format(deadlineFormatter),
                            onLongClick = { contextMenuHomework = homework },
                            onClick = { onHomeworkClick(homework) }
                        )
                    }

                    createList(overdueHomeworks)
                    if (overdueHomeworks.isNotEmpty() && (actualHomeworks.isNotEmpty() || pastHomeworks.isNotEmpty())) {
                        item { Divider() }
                    }
                    createList(actualHomeworks)
                    if (actualHomeworks.isNotEmpty() && pastHomeworks.isNotEmpty()) {
                        item { Divider() }
                    }
                    createList(pastHomeworks)
                }

                DropdownMenu(
                    expanded = (contextMenuHomework != null),
                    onDismissRequest = { contextMenuHomework = null }
                ) {
                    val context = LocalContext.current
                    DropdownMenuItem(
                        onClick = {
                            val homework = checkNotNull(contextMenuHomework)
                            contextMenuHomework = null
                            MaterialAlertDialogBuilder(context)
                                .setMessage(R.string.h_delete_message)
                                .setPositiveButton(R.string.h_delete_yes) { _, _ -> onDeleteHomeworkClick(homework) }
                                .setNegativeButton(R.string.h_delete_no, null)
                                .show()
                        }
                    ) {
                        Text(text = stringResource(R.string.h_delete_homework))
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworksContentRegularPreview() = AppTheme {
    HomeworksContent(
        semesters = listOf(Semesters.regular.name),
        selectedSemester = Semesters.regular.name,
        overdueHomeworks = List(3) { Homeworks.regular },
        actualHomeworks = List(3) { Homeworks.regular },
        pastHomeworks = List(3) { Homeworks.regular },
        onSelectedSemesterChange = {},
        onAddHomeworkClick = {},
        onHomeworkClick = {},
        onDeleteHomeworkClick = {}
    )
}