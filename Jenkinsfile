pipeline {
  agent any

  stages {
    stage('Detekt') {
      steps {
        sh './gradlew printVersion'
        sh './gradlew detekt'
        archiveArtifacts 'build/reports/detekt/detekt.html'
      }
    }
  }
}