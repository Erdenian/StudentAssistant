package com.erdenian.studentassistant.schedule.semestereditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.erdenian.studentassistant.mediator.findComponent
import com.erdenian.studentassistant.schedule.ScheduleApi
import com.erdenian.studentassistant.schedule.api.ScheduleScreen
import com.erdenian.studentassistant.schedule.di.ScheduleComponent
import com.erdenian.studentassistant.schedule.semestereditor.SemesterEditorViewModel.Error
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.uikit.dialog.ProgressDialog
import com.erdenian.studentassistant.utils.toSingleLine
import com.erdenian.studentassistant.utils.toast

internal class SemesterEditorScreen(private val arguments: ScheduleScreen.SemesterEditor) : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel {
            findComponent<ScheduleApi, ScheduleComponent>().semesterEditorViewModelFactory.get(arguments.semesterId)
        }
        val navigator = LocalNavigator.currentOrThrow

        val done by viewModel.done.collectAsState()
        LaunchedEffect(done) {
            if (done) navigator.pop()
        }

        var isNameChanged by rememberSaveable { mutableStateOf(false) }

        val error by viewModel.error.collectAsState()
        val errorMessage = when (error) {
            Error.EMPTY_NAME -> RS.se_error_empty_name
            Error.SEMESTER_EXISTS -> RS.se_error_name_not_available
            Error.WRONG_DATES -> RS.se_error_wrong_dates
            null -> null
        }?.let { stringResource(it) }

        val name by viewModel.name.collectAsState()
        val nameErrorMessage = errorMessage
            ?.takeIf { (error == Error.EMPTY_NAME) && isNameChanged || (error == Error.SEMESTER_EXISTS) }
            ?.takeIf { !done } // Error message flashes before navigating back if the database is fast enough

        val firstDay by viewModel.firstDay.collectAsState()
        val lastDay by viewModel.lastDay.collectAsState()

        val operation by viewModel.operation.collectAsState()

        val isLoading: Boolean
        val isSaving: Boolean
        when (operation) {
            SemesterEditorViewModel.Operation.LOADING -> {
                isLoading = true
                isSaving = false
            }
            SemesterEditorViewModel.Operation.SAVING -> {
                isLoading = false
                isSaving = true
            }
            null -> {
                isLoading = false
                isSaving = false
            }
        }

        if (isSaving) ProgressDialog(stringResource(RS.se_saving))

        val context = LocalContext.current

        SemesterEditorContent(
            isLoading = isLoading,
            isEditing = viewModel.isEditing,
            name = name,
            firstDay = firstDay,
            lastDay = lastDay,
            errorMessage = nameErrorMessage,
            onBackClick = navigator::pop,
            onSaveClick = {
                isNameChanged = true
                errorMessage?.let { context.toast(it) } ?: viewModel.save()
            },
            onNameChange = { value ->
                isNameChanged = true
                viewModel.name.value = value.toSingleLine()
            },
            onFirstDayChange = { viewModel.firstDay.value = it },
            onLastDayChange = { viewModel.lastDay.value = it }
        )
    }
}
