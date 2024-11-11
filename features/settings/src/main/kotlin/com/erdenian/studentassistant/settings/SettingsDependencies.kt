package com.erdenian.studentassistant.settings

import android.app.Application
import com.erdenian.studentassistant.repository.api.RepositoryApi

public interface SettingsDependencies {
    public val application: Application
    public val repositoryApi: RepositoryApi
}
