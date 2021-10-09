package ru.erdenian.studentassistant.ui.main.homeworks

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.format.DateTimeFormat
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.entity.Homework
import ru.erdenian.studentassistant.uikit.style.AppIcons
import ru.erdenian.studentassistant.uikit.style.AppTheme
import ru.erdenian.studentassistant.uikit.views.ActionItem
import ru.erdenian.studentassistant.uikit.views.HomeworkCard
import ru.erdenian.studentassistant.uikit.views.TopAppBarActions
import ru.erdenian.studentassistant.uikit.views.TopAppBarDropdownMenu
import ru.erdenian.studentassistant.utils.Homeworks
import ru.erdenian.studentassistant.utils.Semesters

class HomeworksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(inflater.context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        val viewModel by viewModels<HomeworksViewModel>()

        setContent {
            AppTheme {
                val semesters by viewModel.allSemesters.map { it.list }.observeAsState(emptyList())
                val selectedSemester by viewModel.selectedSemester.observeAsState()

                val overdueHomeworks by viewModel.overdue.map { it.list }.observeAsState(emptyList())
                val actualHomeworks by viewModel.actual.map { it.list }.observeAsState(emptyList())
                val pastHomeworks by viewModel.past.map { it.list }.observeAsState(emptyList())

                val context = LocalContext.current

                HomeworksContent(
                    semesters = semesters.map { it.name },
                    selectedSemester = selectedSemester?.name,
                    overdueHomeworks = overdueHomeworks,
                    actualHomeworks = actualHomeworks,
                    pastHomeworks = pastHomeworks,
                    onSelectedSemesterChange = { viewModel.selectSemester(semesters[it]) },
                    onAddHomeworkClick = {
                        findNavController().navigate(
                            HomeworksFragmentDirections.createHomework(checkNotNull(viewModel.selectedSemester.value).id)
                        )
                    },
                    onHomeworkClick = { findNavController().navigate(HomeworksFragmentDirections.editHomework(it)) },
                    onDeleteHomeworkClick = { homework ->
                        MaterialAlertDialogBuilder(context)
                            .setMessage(R.string.hf_delete_message)
                            .setPositiveButton(R.string.hf_delete_yes) { _, _ -> viewModel.deleteHomework(homework.id) }
                            .setNegativeButton(R.string.hf_delete_no, null)
                            .show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
                    Text(text = stringResource(R.string.hf_title))
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
                                name = stringResource(R.string.hf_add),
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
                text = stringResource(R.string.hf_no_schedule),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.activity_horizontal_margin))
            )
            actualHomeworks.isEmpty() && pastHomeworks.isEmpty() -> Text(
                text = stringResource(R.string.hf_no_homeworks),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.activity_horizontal_margin))
            )
            else -> {
                var contextMenuHomework by remember { mutableStateOf<Homework?>(null) }
                val deadlineFormatter = remember { DateTimeFormat.shortDate() }

                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = dimensionResource(R.dimen.activity_horizontal_margin),
                        vertical = dimensionResource(R.dimen.activity_vertical_margin)
                    ),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.cards_spacing)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    fun LazyListScope.createList(homeworks: List<Homework>) = itemsIndexed(
                        items = homeworks,
                        key = { _, item -> item.id }
                    ) { _, homework ->
                        HomeworkCard(
                            subjectName = homework.subjectName,
                            description = homework.description,
                            deadline = homework.deadline.toString(deadlineFormatter),
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
                                .setMessage(R.string.hf_delete_message)
                                .setPositiveButton(R.string.hf_delete_yes) { _, _ -> onDeleteHomeworkClick(homework) }
                                .setNegativeButton(R.string.hf_delete_no, null)
                                .show()
                        }
                    ) {
                        Text(text = stringResource(R.string.hf_delete_homework))
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeworksContentPreviewRegular() = AppTheme {
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
