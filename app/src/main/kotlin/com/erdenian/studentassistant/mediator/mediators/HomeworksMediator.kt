package com.erdenian.studentassistant.mediator.mediators

import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.homeworks.HomeworksApi
import com.erdenian.studentassistant.homeworks.HomeworksApiComponentHolder
import com.erdenian.studentassistant.homeworks.HomeworksDependencies
import com.erdenian.studentassistant.mediator.Mediator
import com.erdenian.studentassistant.mediator.componentRegistry

object HomeworksMediator : Mediator<HomeworksApi>() {

    override val apiComponentHolder by componentRegistry<HomeworksApi, HomeworksApiComponentHolder> {
        HomeworksApiComponentHolder(object : HomeworksDependencies {
            override val application get() = MainApplication.instance
            override val selectedSemesterRepository get() = RepositoryMediator.api.selectedSemesterRepository
            override val semesterRepository get() = RepositoryMediator.api.semesterRepository
            override val lessonRepository get() = RepositoryMediator.api.lessonRepository
            override val homeworkRepository get() = RepositoryMediator.api.homeworkRepository
        })
    }
}
