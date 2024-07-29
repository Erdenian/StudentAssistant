package com.erdenian.studentassistant.mediator.mediators

import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.homeworks.HomeworksApi
import com.erdenian.studentassistant.homeworks.HomeworksApiHolder
import com.erdenian.studentassistant.homeworks.HomeworksDependencies
import com.erdenian.studentassistant.mediator.Mediator
import com.erdenian.studentassistant.mediator.componentRegistry

object HomeworksMediator : Mediator<HomeworksApi>() {

    override val apiHolder by componentRegistry<HomeworksApi, HomeworksApiHolder> {
        HomeworksApiHolder(object : HomeworksDependencies {
            override val application get() = MainApplication.instance
            override val selectedSemesterRepository get() = RepositoryMediator.api.selectedSemesterRepository
            override val semesterRepository get() = RepositoryMediator.api.semesterRepository
            override val lessonRepository get() = RepositoryMediator.api.lessonRepository
            override val homeworkRepository get() = RepositoryMediator.api.homeworkRepository
        })
    }
}
