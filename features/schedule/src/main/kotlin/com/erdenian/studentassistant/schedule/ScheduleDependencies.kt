package com.erdenian.studentassistant.schedule

import android.app.Application
import com.erdenian.studentassistant.repository.api.RepositoryApi

public interface ScheduleDependencies {
    public val application: Application
    public val repositoryApi: RepositoryApi
}
