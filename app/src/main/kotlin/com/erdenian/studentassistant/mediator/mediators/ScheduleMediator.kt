package com.erdenian.studentassistant.mediator.mediators

import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.mediator.Mediator
import com.erdenian.studentassistant.mediator.componentRegistry
import com.erdenian.studentassistant.schedule.ScheduleApi
import com.erdenian.studentassistant.schedule.ScheduleApiComponentHolder
import com.erdenian.studentassistant.schedule.ScheduleDependencies

object ScheduleMediator : Mediator<ScheduleApi>() {

    override val apiComponentHolder by componentRegistry<ScheduleApi, ScheduleApiComponentHolder> {
        ScheduleApiComponentHolder(object : ScheduleDependencies {
            override val application get() = MainApplication.instance
            override val selectedSemesterRepository get() = RepositoryMediator.api.selectedSemesterRepository
            override val semesterRepository get() = RepositoryMediator.api.semesterRepository
            override val lessonRepository get() = RepositoryMediator.api.lessonRepository
            override val homeworkRepository get() = RepositoryMediator.api.homeworkRepository
            override val settingsRepository get() = RepositoryMediator.api.settingsRepository
        })
    }
}
