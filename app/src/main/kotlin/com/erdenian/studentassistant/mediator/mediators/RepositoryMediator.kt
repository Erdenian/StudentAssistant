package com.erdenian.studentassistant.mediator.mediators

import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.mediator.Mediator
import com.erdenian.studentassistant.mediator.componentRegistry
import com.erdenian.studentassistant.repository.RepositoryApi
import com.erdenian.studentassistant.repository.RepositoryApiComponentHolder
import com.erdenian.studentassistant.repository.RepositoryDependencies

object RepositoryMediator : Mediator<RepositoryApi>() {

    override val apiComponentHolder by componentRegistry<RepositoryApi, RepositoryApiComponentHolder> {
        RepositoryApiComponentHolder(object : RepositoryDependencies {
            override val application get() = MainApplication.instance
        })
    }
}
