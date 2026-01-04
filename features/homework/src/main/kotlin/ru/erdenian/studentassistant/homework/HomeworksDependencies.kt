package ru.erdenian.studentassistant.homework

import android.app.Application
import ru.erdenian.studentassistant.analytics.api.AnalyticsApi
import ru.erdenian.studentassistant.repository.api.RepositoryApi

public interface HomeworksDependencies {
    public val application: Application
    public val repositoryApi: RepositoryApi
    public val analyticsApi: AnalyticsApi
}
