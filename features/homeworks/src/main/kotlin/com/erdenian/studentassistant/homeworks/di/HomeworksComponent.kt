package com.erdenian.studentassistant.homeworks.di

import com.erdenian.studentassistant.homeworks.homeworkeditor.HomeworkEditorViewModel
import com.erdenian.studentassistant.homeworks.homeworks.HomeworksViewModel
import dagger.Subcomponent

@Subcomponent
interface HomeworksComponent {
    fun homeworksViewModel(): HomeworksViewModel
    fun homeworkEditorViewModelFactory(): HomeworkEditorViewModel.Factory
}
