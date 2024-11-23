package com.erdenian.studentassistant.homeworks

import android.app.Application
import com.erdenian.studentassistant.repository.api.RepositoryApi

public interface HomeworksDependencies {
    public val application: Application
    public val repositoryApi: RepositoryApi
}
