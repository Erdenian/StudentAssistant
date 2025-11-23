package ru.erdenian.studentassistant.settings

import android.app.Application
import ru.erdenian.studentassistant.repository.api.RepositoryApi

public interface SettingsDependencies {
    public val application: Application
    public val repositoryApi: RepositoryApi
}
