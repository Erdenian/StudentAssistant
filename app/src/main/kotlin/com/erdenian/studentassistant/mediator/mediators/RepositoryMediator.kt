package com.erdenian.studentassistant.mediator.mediators

import com.erdenian.studentassistant.MainApplication
import com.erdenian.studentassistant.mediator.Mediator
import com.erdenian.studentassistant.mediator.componentRegistry
import com.erdenian.studentassistant.repository.RepositoryApi
import com.erdenian.studentassistant.repository.RepositoryApiHolder
import com.erdenian.studentassistant.repository.RepositoryDependencies

object RepositoryMediator : Mediator<RepositoryApi>() {

    override val apiHolder by componentRegistry<RepositoryApi, RepositoryApiHolder> {
        RepositoryApiHolder(object : RepositoryDependencies {
            override val application get() = MainApplication.instance
        })
    }
}
