package ru.erdenian.studentassistant.schedule

import android.app.Application
import ru.erdenian.studentassistant.repository.api.RepositoryApi

public interface ScheduleDependencies {
    public val application: Application
    public val repositoryApi: RepositoryApi
}
