package ru.erdenian.studentassistant.homeworks

import android.app.Application
import ru.erdenian.studentassistant.repository.api.RepositoryApi

public interface HomeworksDependencies {
    public val application: Application
    public val repositoryApi: RepositoryApi
}
